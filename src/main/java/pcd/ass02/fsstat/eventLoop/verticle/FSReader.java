package pcd.ass02.fsstat.eventLoop.verticle;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.*;

public class FSReader extends AbstractVerticle {
	private final String dir;
	private EventBus eb = null;
	private FileSystem fs = null;

	public FSReader(String dir) {
		this.dir = dir;
	}

	@Override
	public void start() {
		eb = vertx.eventBus();
		fs = vertx.fileSystem();
		getFSReport(dir).onSuccess(size -> eb.publish("finish", null));
	}

	private Future<Void> getFSReport(String path) {
		return fs.props(path).compose(props -> {
			if (props.isDirectory()) {
				return fs.readDir(path).compose(paths -> Future.all(paths.stream().map(this::getFSReport).toList()).map(f -> null));
			} else {
				eb.publish("file-found", (path + props.size()));
				return Future.succeededFuture();
			}
		});
	}
}
