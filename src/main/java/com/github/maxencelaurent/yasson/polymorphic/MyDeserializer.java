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
import jakarta.json.stream.JsonParser;

/**
 * Custom deserializer which can handle polymorphic JSONable object
 *
 * @author Maxence
 */
public class MyDeserializer implements JsonbDeserializer<WithJsonDiscriminator> {


    @Override
    public WithJsonDiscriminator deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        // find discriminator ("@class" property)
        JsonObject value = parser.getObject();
        // theClass is class name the concrete type to be used
        String atClass = value.getString("@class", null);

        System.out.println("Deserialize " + atClass + " as-is " + rtType);

        try {
            // let's load that class
            Class<? extends WithJsonDiscriminator> theClass = Class.forName(atClass).asSubclass(WithJsonDiscriminator.class);
            //Jsonb jsonb = JsonbBuilder.create();
            return MyJsonbProvider.getNotCustomizedJsonb().fromJson(value.toString(), theClass);

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Class not found: " + atClass);
        }

    }
}
