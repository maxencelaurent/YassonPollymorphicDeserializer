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
import java.util.Collection;

/**
 *
 * @author maxence
 */
public class Container implements WithJsonDiscriminator {

    private Collection<WithJsonDiscriminator> list = new ArrayList<>();

    public Collection<WithJsonDiscriminator> getList() {
        return list;
    }

    public void setList(Collection<WithJsonDiscriminator> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Container{" + "list=" + list + '}';
    }
}
