package com.babar.restaurantkiosk.items;

import java.util.ArrayList;
import java.util.List;

public class NodeData {

  public String parent;
  public String name;

    public NodeData(){
        parent = name = "";
    }
    public NodeData(String p1,String n1){
        parent = p1; name = n1;
    }
}
