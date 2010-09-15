/* Copyright (c) 2010 ARtags Project owners (see http://artags.org)
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
package org.artags.android.app.tag;

import org.artags.android.app.util.http.HttpException;
import org.artags.android.app.util.http.HttpUtil;
import java.io.File;
import java.util.HashMap;
import org.artags.android.app.Constants;
import org.artags.android.app.Security;
import org.artags.android.app.util.security.SecurityUtils;

/**
 *
 * @author pierre@artags.org
 */
public class TagUploadService
{

    private static final String VERSION = "1";
    private static final String PLATFORM = "ANDROID";
    private static final String PARAMETER_TITLE = "name";
    private static final String PARAMETER_LATITUDE = "lat";
    private static final String PARAMETER_LONGITUDE = "lon";
    private static final String PARAMETER_LANDSCAPE = "landscape";
    private static final String PARAMETER_VERSION = "version";
    private static final String PARAMETER_PLATFORM = "platform";
    private static final String PARAMETER_KEY = "key";
    private static final String URL_UPLOAD_SERVER = Constants.SERVER + "/upload";

    public static void upload(Tag tag) throws HttpException
    {
        HashMap map = new HashMap();
        map.put(PARAMETER_TITLE, tag.getTitle());
        map.put(PARAMETER_LATITUDE, tag.getLatitude());
        map.put(PARAMETER_LONGITUDE, tag.getLongitude());
        map.put(PARAMETER_VERSION, VERSION );
        map.put(PARAMETER_PLATFORM, PLATFORM );
        map.put(PARAMETER_KEY, SecurityUtils.sha1( tag.getTitle() + Security.KEY_ARTAGS ) );

        if (tag.isLandscape())
        {
            map.put(PARAMETER_LANDSCAPE, "on");
        }
        File file = new File(tag.getFilename());
        HttpUtil.post(URL_UPLOAD_SERVER, map, file, "photo");

    }
}
