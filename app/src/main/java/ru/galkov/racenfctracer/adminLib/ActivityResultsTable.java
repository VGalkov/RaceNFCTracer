package ru.galkov.racenfctracer.adminLib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.galkov.racenfctracer.R;
import ru.galkov.racenfctracer.common.AskResultsTable;

public class ActivityResultsTable  extends Activity {

    private ActivityResultsTableController ARTC;
    private AskResultsTable ART;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_results_table);
        ARTC = new ActivityResultsTableController();
        ARTC.setDefaultView();
        new AskResultsTable(ARTC).execute();;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

// =======================================================

    public class ActivityResultsTableController{
        private Button back_button;
        private Button DownLoadResults;
        public TextView userLogger;

        ActivityResultsTableController() {
            setDefaultView();
        }

        public void setDefaultView() {
            initViewObjects();
            addListeners();
            setDefaultFace();
        }

        private void initViewObjects() {
            back_button =      findViewById(R.id.back_button);
            userLogger =       findViewById(R.id.userLogger);
            DownLoadResults = findViewById(R.id.DownLoadResults);

        }

        private void addListeners() {
            back_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            });

            DownLoadResults.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
// запрос таблицы в текстовом виде.
                }
            });
        }


        // ============================
        private void setDefaultFace() {
// запрос лога регистраций метка = user

        }
    }
}
