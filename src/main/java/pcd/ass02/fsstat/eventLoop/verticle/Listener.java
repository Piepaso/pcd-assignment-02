package pcd.ass02.fsstat.eventLoop.verticle;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.*;

import java.util.HashMap;
import java.util.Map;

import scala.collection.mutable.ListBuffer;

public class Listener extends AbstractVerticle {
	private final Map<String, Integer> allFiles = new HashMap<>();

	@Override
	public void start() {
		var eb = vertx.eventBus();
		eb.consumer("file-found", msg -> allFiles.put(msg.body().toString(), Integer.valueOf(msg.body().toString())));
		eb.consumer("finish", msg -> printReport());
	}

	private void printReport() {
		System.out.println("\n--- REPORT FINALE (${allFiles.size} file) ---");
		allFiles.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).limit(10).forEach(System.out::println);
		vertx.close();
	}

}