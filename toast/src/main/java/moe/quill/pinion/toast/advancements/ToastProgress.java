package moe.quill.pinion.toast.advancements;

import java.util.Map;

public class ToastProgress {
    public Map<String, ToastCriterion> a;
    public String[][] b;

    public ToastProgress(){
    }
    public ToastProgress(Map<String, ToastCriterion> a, String[][] b) {
        this.a = a;
        this.b = b;
    }
}
