package net.bjmsw.uvm.util;

import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.Visitor;

import java.util.List;

public class GarbageCollector {

    // TODO - Only thoughts - NEED ACTUAL WORK
    public void runGarbageCollector() {
        List<Visitor> consoleVisitors = VisitorManager.getApiClient().getVisitors();
        long now = System.currentTimeMillis() / 1000;

        for (Visitor v : consoleVisitors) {
            // Only target visitors created by THIS app
            if (v.getRemarks() != null && v.getRemarks().contains("UVM_EVENT_")) {
                if (v.getEnd_time() < now) {
                    System.out.println("Janitor: Removing expired visitor " + v.getFirstName() + " " + v.getLastName());
                    //VisitorManager.getApiClient().deleteVisitor(v.getId()); // You'll need to add DELETE to your ApiClient
                }
            }
        }
    }

}
