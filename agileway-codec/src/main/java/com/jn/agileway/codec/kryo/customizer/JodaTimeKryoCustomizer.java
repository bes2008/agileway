package com.jn.agileway.codec.kryo.customizer;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.kryo.KryoCustomizer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateTimeSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalTimeSerializer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class JodaTimeKryoCustomizer implements KryoCustomizer {
    @Override
    public String getName() {
        return "joda_time";
    }

    @Override
    public void customize(Kryo kryo) {
        kryo.register(DateTime.class, new JodaDateTimeSerializer());
        kryo.register(LocalDate.class, new JodaLocalDateSerializer());
        kryo.register(LocalDateTime.class, new JodaLocalDateTimeSerializer());
        kryo.register(LocalDateTime.class, new JodaLocalTimeSerializer());
    }
}
