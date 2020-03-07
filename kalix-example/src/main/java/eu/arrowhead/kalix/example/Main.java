package eu.arrowhead.kalix.example;

import eu.arrowhead.kalix.dto.binary.BinaryReader;
import eu.arrowhead.kalix.dto.binary.BinaryWriter;
import eu.arrowhead.kalix.example.dto.PointBuilder;
import eu.arrowhead.kalix.example.dto.ShapeBuilder;
import eu.arrowhead.kalix.example.dto.ShapeData;
import eu.arrowhead.kalix.example.dto.ShapeType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(final String[] args) {
        try {
            System.out.println("Hello, Example!");

            final var map = new HashMap<String, Map<String, Integer>>();

            final var map0 = new HashMap<String, Integer>();
            map0.put("hejsan", 1);
            map0.put("ojsan", 2);
            map.put("hej", map0);

            final var map1 = new HashMap<String, Integer>();
            map.put("tomt", map1);

            final var shape0 = new ShapeBuilder()
                .position(new PointBuilder()
                    .x(1423e134)
                    .y(352234.123432e-142)
                    .build())
                .attributes(Arrays.asList(
                    Arrays.asList((byte) 1, (byte) 2),
                    Arrays.asList((byte) 123, (byte) 0, (byte) -5)))
                .attributes2(1, 2, 3, 4, 5, 6, 7)
                .name("Jaime")
                .bools(true, false, true, true)
                .properties(map)
                .type(ShapeType.TRIANGLE)
                .build();

            final var byteBuffer = ByteBuffer.allocate(4096);
            final var reader = BinaryReader.from(byteBuffer);
            final var writer = BinaryWriter.from(byteBuffer);

            shape0.writeJson(writer);

            final var text0 = new String(byteBuffer.array(), 0, byteBuffer.position(), StandardCharsets.UTF_8);
            System.out.println(text0);

            byteBuffer.flip();
            byteBuffer.position(0);
            final var shape1 = ShapeData.readJson(reader);

            byteBuffer.flip();
            byteBuffer.position(0);
            byteBuffer.limit(4096);
            shape1.writeJson(writer);

            final var text1 = new String(byteBuffer.array(), 0, byteBuffer.position(), StandardCharsets.UTF_8);
            System.out.println(text1);
        }
        catch (final Throwable e) {
            e.printStackTrace();
        }
    }
}
