package org.taktik.icure.applications.utils;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClassScanner {
    private String packagePath;

    private List<Class<? extends Object>> packageClasses;
    private int length;
    private int index;

    public ClassScanner(String packagePath, Class<JsonDeserializer> parentClass){
        this.packagePath = packagePath;

        Reflections reflections = new Reflections(packagePath);

        this.packageClasses = new ArrayList<>(reflections.getSubTypesOf(parentClass));
        this.length = packageClasses.size();
        this.index = 0;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public boolean hasNext(){
        return index < length;
    }

    public Class next() {
        return this.packageClasses.get(this.index++);
    }

    public long getLength() {
        return length;
    }

    public void reset(){
        this.index = 0;
    }

    public boolean seek(int index){
        if(index < this.length) {
            this.index = index;
            return true;
        }
        return false;
    }
}
