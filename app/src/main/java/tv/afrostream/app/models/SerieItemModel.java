package tv.afrostream.app.models;

import org.json.JSONObject;

/**
 * Created by bahri on 18/01/2017.
 */

public class SerieItemModel {
    public String coverImageUrl;
    public String title;
    public String episodeNumber;
    public JSONObject episode_all_Info;
    public JSONObject movie_all_Info;


    public SerieItemModel(String TitleS, String episodeNumberS, String coverImageUrlS, JSONObject allInfoS,JSONObject movie_all_InfoJ)
    {

        this.title=TitleS;
        this.episodeNumber=episodeNumberS;
        this.coverImageUrl=coverImageUrlS;
        this.episode_all_Info=allInfoS;
        this.movie_all_Info=movie_all_InfoJ;
    }
}
