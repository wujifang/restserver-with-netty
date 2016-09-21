package wjf.restserver;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;

public class HttpServerInboundHandler extends ChannelHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HttpServerInboundHandler.class);

	private HttpRequest request;
	String uri;
	String json;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;
			// System.out.println("Uri:" + request.getUri());
			// System.out.println("Method:" + request.getMethod());
			// if (request.getMethod().equals(HttpMethod.GET)) {
			// QueryStringDecoder de = new QueryStringDecoder(request.getUri());
			// String df = de.parameters().get("df").get(0).toString();
			// System.out.println(df);
			// }
			// if (request.getMethod().equals(HttpMethod.POST)) {
			// HttpPostRequestDecoder de = new HttpPostRequestDecoder(factory,
			// request);
			//
			// }
			uri = request.getUri();

		}
		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;
			ByteBuf buf = content.content();
			// System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
			json = buf.toString(io.netty.util.CharsetUtil.UTF_8);
			buf.release();
			Object resp = ProcessReq.getInstance().process(uri, json);
			if (resp == null)
				ctx.flush();
			else {
				// TODO 这里要把对象序列化为json
				FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
						Unpooled.wrappedBuffer(resp.toString().getBytes("UTF-8")));
				response.headers().set(CONTENT_TYPE, "text/plain");
				response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
				if (HttpHeaders.isKeepAlive(request)) {
					response.headers().set(CONNECTION, Values.KEEP_ALIVE);
				}
				ctx.write(response);
				ctx.flush();
			}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error(cause.getMessage());
		ctx.close();
	}
}