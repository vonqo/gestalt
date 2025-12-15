package mn.von.gestalt.utility.grimoire;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Represents a single point with 3D coordinates and an RGB color.
 */

public class PolygonUtils {

    public static void writePlyHeader(BufferedWriter writer, int numVertices) throws IOException {
        writer.write("ply\n");
        writer.write("format binary_little_endian 1.0\n");
        writer.write("comment created by vonqo\n");

        writer.write("element vertex " + numVertices + "\n");
        writer.write("property float x\n");
        writer.write("property float y\n");
        writer.write("property float z\n");

        writer.write("property uchar red\n");
        writer.write("property uchar green\n");
        writer.write("property uchar blue\n");
        writer.write("property uchar a\n");
        writer.write("end_header\n");
    }

    /**
     * Writes point cloud data to a BINARY LITTLE ENDIAN PLY file.
     * @param points The List of PointData objects.
     * @param file The file to create (e.g., "point_cloud_binary.ply").
     * @throws IOException If an error occurs during file writing.
     */
    public static void createBinaryPlyFile(List<PointData> points, File file) throws IOException {
        int numVertices = points.size();

        // 1. Write the ASCII Header first
        try (BufferedWriter headerWriter = new BufferedWriter(new FileWriter(file))) {
            writePlyHeader(headerWriter, numVertices);
        }

        // 2. Append the Binary Data after the Header
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(file, true)))) { // 'true' means append mode

            // This ByteBuffer is used to convert float/double values into little-endian bytes
            ByteBuffer buffer = ByteBuffer.allocate(4); // Allocate space for a float
            buffer.order(ByteOrder.LITTLE_ENDIAN); // Set the byte order

            for (PointData point : points) {

                // --- Write X, Y, Z (float, 4 bytes each) ---

                // Write X
                buffer.clear();
                buffer.putFloat((float) point.x);
                dos.write(buffer.array());

                // Write Y
                buffer.clear();
                buffer.putFloat((float) point.y);
                dos.write(buffer.array());

                // Write Z
                buffer.clear();
                buffer.putFloat((float) point.z);
                dos.write(buffer.array());

                // --- Write R, G, B, A (uchar, 1 byte each) ---

                // writeByte writes an 8-bit byte value, which matches the 'uchar' property
                dos.writeByte(point.red);
                dos.writeByte(point.green);
                dos.writeByte(point.blue);
                dos.writeByte(point.alpha);
            }
        }

        System.out.println("Successfully created BINARY LITTLE ENDIAN PLY file: " + file.getAbsolutePath() + " with " + numVertices + " points.");
    }

}
