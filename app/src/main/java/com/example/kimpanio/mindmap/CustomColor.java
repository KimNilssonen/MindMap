package com.example.kimpanio.mindmap;

/**
 * Created by Kimpanio on 2017-04-23.
 */

public class CustomColor {

    private String _name;
    private String _hex;

    public CustomColor(String name, String hex){
        _name = name;
        _hex = hex;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getHex() {
        return _hex;
    }

    public void setHex(String _hex) {
        this._hex = _hex;
    }


}
