package tv.afrostream.app.models;

import org.json.JSONObject;

/**
 * Created by bahri on 16/01/2017.
 */

public class MovieItemModel {
   public String coverImageUrl;
    public String title;
    public String label;
    public JSONObject movie_all_Info;
    public String pathImage;
    public String pathJsonFile;
    public String pathvideoFile;


        public MovieItemModel(String TitleS,String LabelS,String coverImageUrlS,JSONObject allInfoS)
        {

            this.title=TitleS;
            this.label=LabelS;
            this.coverImageUrl=coverImageUrlS;
            this.movie_all_Info=allInfoS;
            this.pathImage="";
        }

    public MovieItemModel(String TitleS,String LabelS,String coverImageUrlS,JSONObject allInfoS,String pathImage,String pathJsonFile,String pathvideoFile)
    {

        this.title=TitleS;
        this.label=LabelS;
        this.coverImageUrl=coverImageUrlS;
        this.movie_all_Info=allInfoS;
        this.pathImage=pathImage;
        this.pathJsonFile=pathJsonFile;
        this.pathvideoFile=pathvideoFile;
    }
}
