/* Copyright (c) 2010-2011 ARTags Project owners (see http://www.artags.org)
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

package org.artags.android.widget;

/**
 *
 * @author pierre
 */
public class BestTagsIntentService extends AbstractTagsIntentService
{
    private static final String NAME = "BestTagsIntentService";


    public BestTagsIntentService()
    {
        super( NAME );
    }

    @Override
    String getTagListUrl()
    {
        return Constants.URL_BEST_TAGS;
    }

    @Override
    void updateTag(Tag tag)
    {
        BestTagsWidget.updateTag(tag);
    }

}
