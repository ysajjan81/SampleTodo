package com.example.sampletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static  final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    Button addButton;
    EditText editText;
    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        items = new ArrayList<>();
//        items.add("milk");
//        items.add("bread");
//        items.add("eggs");
        loadItems();
        addButton = findViewById(R.id.buttonAdd);
        editText  = findViewById(R.id.edItem);
        recyclerView = findViewById(R.id.rvItems);
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongCLicked(int postion) {
                items.remove(postion);
                itemsAdapter.notifyItemRemoved(postion);
                Toast.makeText(getApplicationContext(), "Item Removed Successfully from the list", Toast.LENGTH_LONG).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int posoition) {
                    Log.d("MainActivity", "Single click at  psoition " + posoition);
                    Intent i = new Intent(MainActivity.this, EditAcitivity.class);
                    i.putExtra(KEY_ITEM_TEXT, items.get(posoition));
                    i.putExtra(KEY_ITEM_POSITION, posoition);
                    startActivityForResult(i, EDIT_TEXT_CODE);

            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listItem = editText.getText().toString();
                items.add(listItem);
                itemsAdapter.notifyItemInserted(items.size()-1);
                editText.setText("");
                Toast.makeText(getApplicationContext(), "Item Added Successfully to the list", Toast.LENGTH_LONG).show();
                saveItems();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE)
        {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item Updated successfully", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.w("MainActivity", "Unknow call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
            items = new ArrayList<>();
        }
    }
    private void saveItems() {
        try {
           FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            e.printStackTrace();
//            items = new ArrayList<>();
        }
    }

}