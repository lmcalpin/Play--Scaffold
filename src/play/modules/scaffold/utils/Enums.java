/**
 *
 * Copyright 2010, Lawrence McAlpin.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package play.modules.scaffold.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Enums {

    public static <T extends Enum<T>> List<String> list(T[] values) {
        List<String> returnMe = new ArrayList<String>();
        for (T value : values) {
            returnMe.add(value.name());
        }
        return returnMe;
    }

    @SuppressWarnings("unchecked")
    public static Enum[] values(Class<Enum> clazz) {
        try {
            Method method = clazz.getMethod("values", new Class[] {});
            Object retValue = method.invoke(clazz, new Object[] {});
            Enum[] enums = (Enum[]) retValue;
            return enums;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
