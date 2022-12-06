package com.babar.restaurantkiosk.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.babar.restaurantkiosk.R;
import com.babar.restaurantkiosk.adapters.DirectoryNodeBinder;
import com.babar.restaurantkiosk.adapters.FileNodeBinder;
import com.babar.restaurantkiosk.adapters.StorageFolderRecyclerViewAdapter;
import com.babar.restaurantkiosk.items.Dir;
import com.babar.restaurantkiosk.items.NodeData;
import com.babar.restaurantkiosk.managers.MyFDBManager;
import com.babar.restaurantkiosk.managers.MyWorkerManager;
import com.babar.restaurantkiosk.managers.StorageManager;
import com.babar.restaurantkiosk.models.UserInfoModel;
import com.babar.restaurantkiosk.util.DebugLog;
import com.babar.restaurantkiosk.util.LocalCache;
import com.babar.restaurantkiosk.util.RKUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

import static com.babar.restaurantkiosk.managers.StorageManager.ROOT_REF;

public class ChangeStorageFolderScreen extends AppCompatActivity {
    List<String> folders = new ArrayList<>();
    RecyclerView recyclerView;
    private int checkBoxPosition = -1;
    private TreeViewAdapter adapter;
    List<TreeNode> nodes = new ArrayList<>();
    List<NodeData> allNodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_folder_screen);
        initView();
        initData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTreeData();
                setAdapterTreeView();
                adapter.notifyDataSetChanged();
            }
        },1000);

    }

    public void initView() {
        recyclerView = findViewById(R.id.folderRV);
    }

    public void initData() {
        listWholeData(null,null);
    }



    public void setTreeData(){

        // clear...
        nodes.clear();
        // First, set collection in tree view...
        List<String> collections =  getMatchedNodes(ROOT_REF);
        int collection_size = collections.size();
        DebugLog.console("collection size:" + collection_size);
        for (int i = 0 ; i < collection_size ; i++){
            String subKey = collections.get(i);
            DebugLog.console("sub key ----------------------------------:" + subKey);
            TreeNode<Dir> subNode = new TreeNode<>(new Dir(subKey));
            if(!isLeaf(subKey)) addSubNode(subNode,subKey);
            nodes.add(subNode);
        }
    }

    public void addSubNode( TreeNode<Dir> subNode, String subKey ){
        // With each key, create Tree Node ... recursive function use.
        List<String> subs =  getMatchedNodes(subKey);
        int size = subs.size();
        DebugLog.console("addSubNode subkey:-----------" + subKey);
        for (int i = 0 ; i < size ; i++){
            String skey = subs.get(i);
            DebugLog.console("addSubNode:-------------" + skey);
            TreeNode<Dir> child = new TreeNode<>(new Dir(subKey));
            if(!isLeaf(skey)) addSubNode(child,skey);
            subNode.addChild(child);
        }

    }

    // get key Matched Nodes...
    public List<String> getMatchedNodes(String key){
        List<String> subNodes =  new ArrayList<>();
        for (int i = 0; i < allNodes.size() ; i++){
            if (allNodes.get(i).parent.equals(key)) subNodes.add(allNodes.get(i).name);
        }
        return subNodes;
    }

    // check isLeaf using key : eg.. CMPC, Ricardo Pizzaria...
    public boolean isLeaf(String key){
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).parent.equals(key)) return false;
        }
        return  true;
    }


    public void setAdapterTreeView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreeViewAdapter(nodes, Arrays.asList(new DirectoryNodeBinder(), new DirectoryNodeBinder()));
        // whether collapse child nodes when their parent node was close.
        adapter.ifCollapseChildWhileCollapseParent(true);
        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {

//                DirectoryNodeBinder.ViewHolder dirViewHolder = (DirectoryNodeBinder.ViewHolder) holder;
//                final CheckBox ivArrow = dirViewHolder.getIvArrow();
//                if(node.isExpand()){   } else{
//                    if (!node.isLeaf()) {
//                        //Update and toggle the node.
//                         if (!node.isExpand())
//                             adapter.collapseBrotherNode(node);
//                        // Load data from firebase...
//                    }else{
//                        listAllChildren(node);
//                    }
//                }
//
////                boolean isCheck = !node.isExpand() ? true : false;
//                ivArrow.setChecked(true);
////                if(bCheck!= null && bCheck!= ivArrow) bCheck.setChecked(false);
////                bCheck = ivArrow;
////                curNode = node;
////                checkBoxPosition = 1;
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    public Dir getDirFromNode(TreeNode node){
        Dir curDir = (Dir) node.getContent();
        return curDir;
    }

    public List<String> getFirebasePath(TreeNode node){
        ArrayList<String> arrPaths =  new ArrayList<>();
        if(!node.isRoot()) {
            while (!node.isRoot()) {
                arrPaths.add(getDirFromNode(node).dirName);
                if (node.getParent() != null)
                    node = node.getParent();
            }
        }
        arrPaths.add(getDirFromNode(node).dirName);
        return arrPaths;
    }

    public String getFullPath(List<String> arrPaths){
        String fullPath = "";
        String link = "/";
        int size = arrPaths.size();
        DebugLog.console("arrPaths size:" + arrPaths.size());
        for(int i = 0 ; i < size ; i++){
            if(i == size - 1) link ="";
             fullPath += arrPaths.get(size - i -1) + link;
         }
        return fullPath;
    }

    public void closeFolder(View v){
        finish();
    }

//    public void saveFolder(View v){
//        String path = getFullPath(getFirebasePath(curNode));
//        if(checkBoxPosition!=-1){
//            DebugLog.console("saveFolder:" + path);
//            LocalCache.setStorageFolder(path);
//            String deviceId = RKUtil.getDeviceId(this);
//            UserInfoModel userInfoModel = new UserInfoModel(LocalCache.getPassword(), LocalCache.getDuration(), LocalCache.getStorageFolder(), deviceId, LocalCache.getTouchWaitingDuration(), RKUtil.UPDATE_FLAG);
//            RKUtil.uploadDeviceInfo(this, userInfoModel);
//            MyWorkerManager.getInstance().oneTimeSendMessage();
//        }
//        finish();
//    }

    public void listWholeData(String key,StorageReference sRef){

        StorageReference subRef;
        if(sRef == null && key == null){ // initial call...
            subRef = FirebaseStorage.getInstance().getReference(ROOT_REF);
            key = ROOT_REF;
        } else  subRef = sRef.child(key);

        String finalKey = key;
        subRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> results = listResult.getPrefixes();
                if(results.isEmpty() || results == null) return;
                for (StorageReference result : results) {
                    listWholeData(result.getName(),subRef);
                    allNodes.add(new NodeData(finalKey,result.getName()));
                    DebugLog.console(finalKey + ":" + result.getName() + "-----------" + allNodes.size());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DebugLog.console("[ChangeStorageFolderScreen] inside onFailure() "+e.toString());
            }
        });
    }


    public void listAllFolders(){
        StorageManager.getInstance(this).getFolders().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> sfolders = listResult.getPrefixes();
                for (StorageReference folder : sfolders) {
                    nodes.add(new TreeNode<>(new Dir(folder.getName())));
                }
                setAdapterTreeView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DebugLog.console("[ChangeStorageFolderScreen] inside onFailure() "+e.toString());
            }
        });
    }

    public void listAllChildren(TreeNode node){
        List<String> arrPath =  getFirebasePath(node);
        StorageManager.getInstance(this).getFolders(arrPath).addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if(node.getChildList() != null) node.getChildList().clear();
                List<StorageReference> sfolders = listResult.getPrefixes();
                for (StorageReference folder : sfolders) {
                    TreeNode<Dir> tempDir = new TreeNode<>(new Dir(folder.getName()));
                    node.addChild(tempDir);
                }
                if(sfolders.size() > 0) {
                    adapter.notifyDataSetChanged();
                    DebugLog.console("notifyDataSetchanged");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DebugLog.console("[ChangeStorageFolderScreen] inside onFailure() "+e.toString());
            }
        });
    }


}