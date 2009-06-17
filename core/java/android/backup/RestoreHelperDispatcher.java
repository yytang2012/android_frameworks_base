/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.backup;

import java.io.IOException;
import java.util.HashMap;

/** @hide */
public class RestoreHelperDispatcher {
    HashMap<String,RestoreHelper> mHelpers = new HashMap<String,RestoreHelper>();

    public void addHelper(String keyPrefix, RestoreHelper helper) {
        mHelpers.put(keyPrefix, helper);
    }

    public void dispatch(BackupDataInput input) throws IOException {
        BackupDataInputStream stream = new BackupDataInputStream(input);
        while (input.readNextHeader()) {
            String rawKey = input.getKey();
            int pos = rawKey.indexOf(':');
            if (pos > 0) {
                String prefix = rawKey.substring(0, pos);
                RestoreHelper helper = mHelpers.get(prefix);
                if (helper != null) {
                    stream.dataSize = input.getDataSize();
                    stream.key = rawKey.substring(pos+1);
                    helper.restoreEntity(stream);
                }
            }
            input.skipEntityData(); // In case they didn't consume the data.
        }
    }
}
