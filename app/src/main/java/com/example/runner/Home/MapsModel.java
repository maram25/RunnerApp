package com.example.runner.Home;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsModel {
    @SerializedName("routes")
    public List<Routes> routes;

    public class Routes{
        @SerializedName("legs")
        public List<Legs> legs;

        public class Legs{
            @SerializedName("distance")
             public Data Dis;


            @SerializedName("duration")
            public Data Dur;
            public class Data{
                @SerializedName("text")
                private  String Text;

                @SerializedName("value")
                private  float Value;

                public float getVlue() {
                    return Value;
                }

                public String getText() {
                    return Text;
                }
            }

        }

    }
}
