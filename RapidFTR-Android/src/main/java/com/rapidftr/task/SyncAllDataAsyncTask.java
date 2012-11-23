package com.rapidftr.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SyncAllDataAsyncTask extends AsyncTask<Void, String, Boolean> {

    private FormService formService;
    private ChildService childService;
    private ChildRepository childRepository;
    private ProgressDialog progressDialog;
    private RapidFtrActivity context;

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository) {
        this.formService = formService;
        this.childService = childService;
        this.childRepository = childRepository;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Starting sync...");
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... notRelevant) {
        try {
            progressDialog.setMessage("Step 1 of 3 - Syncing Form Sections...");
            formService.getPublishedFormSections();
            publishProgress("Step 2 of 3 - Sending records to server...");
            List<Child> childrenToSyncWithServer = childRepository.toBeSynced();
            sendChildrenToServer(childrenToSyncWithServer);
            publishProgress("Step 3 of 3 - Bringing down records from server...");
            saveIncomingChildren();
            publishProgress("Sync complete.");
        } catch (Exception e) {
            publishProgress("Sync failed. please try again.");
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(String... progressMessages) {
        progressDialog.setMessage(progressMessages[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private void sendChildrenToServer(List<Child> childrenToSyncWithServer) {
        for (Child child : childrenToSyncWithServer) {
            try {
                childService.sync(child);
            } catch (Exception e) {
                // TODO: Handle error
            }
        }
    }

    private void saveIncomingChildren() throws IOException, JSONException, GeneralSecurityException {
        for (Child incomingChild : childService.getAllChildren()) {
            incomingChild.setSynced(true);
            if(childRepository.exists(incomingChild.getUniqueId())){
                childRepository.update(incomingChild);
            }else{
                childRepository.createOrUpdate(incomingChild);
            }
         childService.setPhoto(incomingChild);
        }
    }

    public void setContext(RapidFtrActivity context) {
        this.context = context;
    }
}
