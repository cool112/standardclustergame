package com.garow.proto.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;

/**
 * protobuf工具
 * 
 * @author seg
 *
 */
public class ProtobufUtil {
	/**
	 * 生成java protobuf代码，需安装protoc
	 * 参数举例：src\main\resources\proto src\main\java
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			System.out.println("help: cmd <searchDir> <outputDir>");
			return;
		}
		String searchDir = args[0];
		String outputDir = args[1];
		final List<String> protoFiles = new ArrayList<String>();
		try {
			Files.walkFileTree(Paths.get(searchDir), new FileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.toFile().isDirectory())
						return FileVisitResult.CONTINUE;
					if (file.getFileName().toString().endsWith(".proto")) {
						System.out.println(file.toString());
						protoFiles.add(file.toString());
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.SKIP_SIBLINGS;
				}

			});
			for (String file : protoFiles) {
				Process p = Runtime.getRuntime().exec(new String[] { "protoc", file, "--java_out=" + outputDir });
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					System.err.println(line);
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * 发送协议编码，pid小端编码
	 * 
	 * @param pid
	 * @param sendable
	 * @return
	 */
	public static byte[] toByteArray(short pid, GeneratedMessageV3 sendable) {
		byte[] data = sendable.toByteArray();
		byte[] newBytes = new byte[2 + data.length];
		newBytes[0] = (byte) (pid & 0xff);
		newBytes[1] = (byte) ((pid & 0xff00) >> 8);
		System.arraycopy(data, 0, newBytes, 2, data.length);
		return newBytes;
	}
}
