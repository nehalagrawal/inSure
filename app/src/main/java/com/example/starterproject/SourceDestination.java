package com.example.starterproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

public class SourceDestination extends AppCompatActivity {

    private String Source;
    private String Destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_destination);
    }

    public String getSource()
    {
        return Source;
    }

    public String getDestination()
    {
        return Destination;
    }

    final EditText source = (EditText)findViewById(R.id.source);
    final EditText destination = (EditText)findViewById(R.id.destination);
    // final Button update = (Button)findViewById(R.id.update);

    public void update (View view)
    {
        this.Source = source.getText().toString();
        this.Destination = destination.getText().toString();
        Intent intent = new Intent(SourceDestination.this, MainActivity.class);
        intent.putExtra("source", Source);
        intent.putExtra("destination", Destination);
        startActivity(intent);
    }
}
