package tv.afrostream.app.models;

import java.util.ArrayList;

/**
 * Created by bahri on 16/01/2017.
 */

public class CatMoviesModel {

    public String categorie;
    public ArrayList<MovieItemModel> Movies ;


    public CatMoviesModel(String categorieS, ArrayList<MovieItemModel> MoviesS)
    {
        categorie=categorieS;
        Movies=MoviesS;
    }
}
