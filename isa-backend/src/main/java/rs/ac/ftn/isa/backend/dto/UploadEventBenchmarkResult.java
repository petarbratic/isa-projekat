package rs.ac.ftn.isa.backend.dto;

/**
 * Result of JSON vs Protobuf benchmark (serialization time, deserialization time, message size).
 */
public class UploadEventBenchmarkResult {

    private int messageCount;

    private double jsonAvgSerializeMs;
    private double jsonAvgDeserializeMs;
    private double jsonAvgSizeBytes;

    private double protobufAvgSerializeMs;
    private double protobufAvgDeserializeMs;
    private double protobufAvgSizeBytes;

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public double getJsonAvgSerializeMs() {
        return jsonAvgSerializeMs;
    }

    public void setJsonAvgSerializeMs(double jsonAvgSerializeMs) {
        this.jsonAvgSerializeMs = jsonAvgSerializeMs;
    }

    public double getJsonAvgDeserializeMs() {
        return jsonAvgDeserializeMs;
    }

    public void setJsonAvgDeserializeMs(double jsonAvgDeserializeMs) {
        this.jsonAvgDeserializeMs = jsonAvgDeserializeMs;
    }

    public double getJsonAvgSizeBytes() {
        return jsonAvgSizeBytes;
    }

    public void setJsonAvgSizeBytes(double jsonAvgSizeBytes) {
        this.jsonAvgSizeBytes = jsonAvgSizeBytes;
    }

    public double getProtobufAvgSerializeMs() {
        return protobufAvgSerializeMs;
    }

    public void setProtobufAvgSerializeMs(double protobufAvgSerializeMs) {
        this.protobufAvgSerializeMs = protobufAvgSerializeMs;
    }

    public double getProtobufAvgDeserializeMs() {
        return protobufAvgDeserializeMs;
    }

    public void setProtobufAvgDeserializeMs(double protobufAvgDeserializeMs) {
        this.protobufAvgDeserializeMs = protobufAvgDeserializeMs;
    }

    public double getProtobufAvgSizeBytes() {
        return protobufAvgSizeBytes;
    }

    public void setProtobufAvgSizeBytes(double protobufAvgSizeBytes) {
        this.protobufAvgSizeBytes = protobufAvgSizeBytes;
    }
}
