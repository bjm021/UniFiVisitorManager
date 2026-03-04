package net.bjmsw.uvm.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bjmsw.uvm.model.Event;
import org.jspecify.annotations.NonNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;

/**
 * Handles serialization and deserialization of {@link Event} objects into and from JSON format.
 * This class implements the {@link Serializer} interface, providing custom logic for processing
 * {@link Event} instances.
 *
 * <ul>
 *   <li>The serialization process converts an {@link Event} object into its JSON representation.</li>
 *   <li>The deserialization process reconstruces an {@link Event} object from its JSON representation.</li>
 * </ul>
 *
 * Thread-safety: This class is thread-safe due to the immutability of the static {@link ObjectMapper} instance.
 */
public class EventSerializer implements Serializer<Event> {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);


    @Override
    public void serialize(@NonNull DataOutput2 out, @NonNull Event value) throws IOException {
        String json = mapper.writeValueAsString(value);
        out.writeUTF(json);
    }

    @Override
    public Event deserialize(@NonNull DataInput2 input, int available) throws IOException {
        String json = input.readUTF();
        return mapper.readValue(json, Event.class);
    }
}
