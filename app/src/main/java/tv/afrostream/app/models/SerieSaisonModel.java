package tv.afrostream.app.models;

import java.util.ArrayList;

/**
 * Created by bahri on 18/01/2017.
 */


public class SerieSaisonModel {

    public String categorie;
    public ArrayList<SerieItemModel> Movies ;


    public SerieSaisonModel(String categorieS, ArrayList<SerieItemModel> MoviesS)
    {
        categorie=categorieS;
        Movies=MoviesS;
    }
}
