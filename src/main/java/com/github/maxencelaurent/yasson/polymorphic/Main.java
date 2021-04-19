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

import java.util.ArrayList;
import jakarta.json.bind.Jsonb;

/**
 *
 * @author maxence
 */
public class Main {


    private static void toAndFromJson(Jsonb jsonb, WithJsonDiscriminator object, Class<? extends WithJsonDiscriminator>... types) {
        System.out.println("Process " + object);
        String json = jsonb.toJson(object);
        System.out.println(" json -> " + json);

        for (Class<? extends WithJsonDiscriminator> type : types) {
            System.out.println("Deserialize as " + type);
            try {
                WithJsonDiscriminator fromJson = jsonb.fromJson(json, type);
                System.out.println(" -> give " + fromJson);
            } catch (Exception ex) {
                System.err.println(" -> failed with " + ex);
            }
        }
    }

    public static void main(String... args) {
        Jsonb jsonb = MyJsonbProvider.getInternalHackJsonb();
        jsonb =MyJsonbProvider.getNotCustomizedJsonb();

        Container container = new Container();
        container.getList().add(new EntityImpl());
        toAndFromJson(jsonb, container, WithJsonDiscriminator.class, Container.class);

        Wrapper wrapper = new Wrapper();
        wrapper.setPayload(new EntityImpl());
        toAndFromJson(jsonb, wrapper, WithJsonDiscriminator.class, Wrapper.class);

        SubContainer oContainer = new SubContainer();
        oContainer.setList(new ArrayList<>());
        oContainer.getList().add(new EntityImpl());
        toAndFromJson(jsonb, oContainer, WithJsonDiscriminator.class, SubInterface.class, SubContainer.class);
    }
}
