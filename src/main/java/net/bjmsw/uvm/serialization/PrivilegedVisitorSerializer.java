package net.bjmsw.uvm.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bjmsw.uvm.model.PrivilegedVisitor;
import org.jspecify.annotations.NonNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;

/**
 * Handles the serialization and deserialization of {@link PrivilegedVisitor} objects to and from JSON format.
 * This implementation uses the Jackson ObjectMapper for converting the object and ensures that the serialized
 * data includes all fields while ignoring getters and setters.
 */
public class PrivilegedVisitorSerializer implements Serializer<PrivilegedVisitor> {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);


    @Override
    public void serialize(@NonNull DataOutput2 out, @NonNull PrivilegedVisitor value) throws IOException {
        String json = mapper.writeValueAsString(value);
         out.writeUTF(json);
    }

    @Override
    public PrivilegedVisitor deserialize(@NonNull DataInput2 input, int available) throws IOException {
        String json = input.readUTF();
        return mapper.readValue(json, PrivilegedVisitor.class);
    }
}
