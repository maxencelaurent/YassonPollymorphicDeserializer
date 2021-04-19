/*
 * Copyright (C) 2021 maxence
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.maxencelaurent.yasson.polymorphic;

import java.lang.reflect.Type;
import jakarta.json.JsonObject;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import java.io.StringReader;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.serializer.DeserializerBuilder;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Custom deserializer which can handle polymorphic JSONable object
 *
 * @author Maxence
 */
public class InternalHackDeserializer implements JsonbDeserializer<WithJsonDiscriminator> {

    public static class DummyCustomization implements Customization {

        @Override
        public JsonbNumberFormatter getSerializeNumberFormatter() {
            return null;
        }

        @Override
        public JsonbNumberFormatter getDeserializeNumberFormatter() {
            return null;
        }

        @Override
        public JsonbDateFormatter getSerializeDateFormatter() {
            return null;
        }

        @Override
        public JsonbDateFormatter getDeserializeDateFormatter() {
            return null;
        }

        @Override
        public boolean isNillable() {
            return true;
        }
    }

    private static Customization dummyCustomization = new DummyCustomization();

    @Override
    public WithJsonDiscriminator deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        // find discriminator ("@class" property)
        JsonObject value = parser.getObject();
        // theClass is class name the concrete type to be used
        String atClass = value.getString("@class", null);
        System.out.println("Deserialize " + atClass + " with internalHack " + rtType);
        try {
            // let's load that class
            Class<? extends WithJsonDiscriminator> theClass = Class.forName(atClass).asSubclass(WithJsonDiscriminator.class);

            // At this point, parser advanced to the END_OBJECT event and it's not possible to go back to the START_OBJECT event
            // We have to rebuild one from json string:
            JsonProvider provider = JsonProvider.provider();
            JsonParser brandNewParser = provider.createParser(new StringReader(value.toString()));
            JsonbRiParser newParser = new JsonbRiParser(brandNewParser);
            // and move to the START_OBJECT event
            newParser.next();

            // Then, one would simply use ctx.deserialize(newParser, theClass). This does not work
            // and leads to StackOverflowError because ctx.deserialise will call this very method.
            // To fix that, the default ObjectDeserializer shall be user, as it would be without
            // any custom deserializer. Sadly, ObjectDeserializer is package protected...
            // Let's get one anyway...
            JsonbContext jsonbContext = new JsonbContext(MyJsonbProvider.getConfig(), provider);

            JsonbDeserializer<?> objectDeserializer = new DeserializerBuilder(jsonbContext)
                .withRuntimeType(theClass)
                // Here is the main point: to avoid using this UserDeserializer again and again,
                // some dummy customization must be provided.
                .withCustomization(dummyCustomization)
                .withJsonValueType(JsonParser.Event.START_OBJECT)
                .build();

            return (WithJsonDiscriminator) objectDeserializer.deserialize(newParser, ctx, theClass);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Class not found: " + atClass);
        }
    }
}
