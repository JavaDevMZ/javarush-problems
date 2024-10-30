package abstractlist;

import java.io.Serializable;
import java.util.*;

public class CustomTree extends AbstractList<String> implements Cloneable, Serializable{

    Entry<String> root;

     public CustomTree() {
       root = new Entry<>("Root element");
}

public boolean add(String elementName){
Entry<String> newEntry = new Entry<>(elementName);
Queue<Entry<String>> toCheck = new ArrayDeque<>();
Entry<String> currentEntry = root;

while(currentEntry!=null && !currentEntry.isAvailableToAddChildren()){
       if(currentEntry.leftChild!=null){
toCheck.add(currentEntry.leftChild);
                  }
       if(currentEntry.rightChild!=null){
toCheck.add(currentEntry.rightChild);
                            }
            currentEntry = toCheck.poll();
}
        if(currentEntry==null){
            addAvailableToHaveChildren();
            return add(elementName);
        }
if(currentEntry.availableToAddLeftChildren){
   currentEntry.leftChild = newEntry;
   currentEntry.availableToAddLeftChildren=false;
}else{
currentEntry.rightChild = newEntry;
 currentEntry.availableToAddRightChildren=false;
}
   newEntry.parent=currentEntry;
return true;
}

        private void addAvailableToHaveChildren(){
           Entry<String> currentEntry = root;
           Queue<Entry<String>> toCheck = new ArrayDeque<>();
               while(!currentEntry.isAvailableToAddChildren()){
       if(currentEntry.leftChild==null){
currentEntry.availableToAddLeftChildren=true;
          return;
                             }
       if(currentEntry.rightChild==null){
currentEntry.availableToAddRightChildren = true;
            return;
                        }
                if(currentEntry.leftChild!=null){
toCheck.add(currentEntry.leftChild);
                          }
       if(currentEntry.rightChild!=null){
toCheck.add(currentEntry.rightChild);
                 }
            currentEntry = toCheck.poll();
}
        }
@Override
public int size() {
return subTreeSize(root)-1;
}

private int subTreeSize(Entry<String> root){
int leftSize = root.leftChild!=null?subTreeSize(root.leftChild):0;
int rightSize = root.rightChild!=null?subTreeSize(root.rightChild):0;
return leftSize+rightSize+1;
}

public String getParent(String s){
Queue<Entry<String>> toCheck = new ArrayDeque<>();
Entry<String> currentEntry = root;
while(currentEntry!=null&&!currentEntry.elementName.equals(s)){
   if(currentEntry.leftChild!=null)
    toCheck.add(currentEntry.leftChild);
   if(currentEntry.rightChild!=null)
    toCheck.add(currentEntry.rightChild);
currentEntry = toCheck.poll();
}
if(currentEntry!=null && currentEntry.parent!=null){
return currentEntry.parent.elementName;
}else{
return null;
}
}

     public boolean remove(Object o){
         if(!(o instanceof String)){
           throw new UnsupportedOperationException("Wrong element");
         }
        Queue<Entry<String>> toCheck = new ArrayDeque<>();
Entry<String> currentEntry = root;
while(currentEntry!=null && !(currentEntry.elementName.equals(o))){
   if(currentEntry.leftChild!=null)
    toCheck.add(currentEntry.leftChild);
   if(currentEntry.rightChild!=null)
    toCheck.add(currentEntry.rightChild);

                    currentEntry = toCheck.poll();
}
                if(currentEntry!=null){
                  Entry<String> parent = currentEntry.parent;
       if(parent.leftChild!=null && parent.leftChild.elementName.equals(o)){
             parent.leftChild = null;
          //    currentEntry.parent.availableToAddLeftChildren=true;
                    }
        if(parent.rightChild!=null && parent.rightChild.elementName.equals(o)){
             parent.rightChild = null;
            //  currentEntry.parent.availableToAddRightChildren=true;
                }
                    currentEntry.parent=null;
                    return true;
     }
                return false;
}

@Override
public String get(int index) {
throw new UnsupportedOperationException("Not supported.");
}

@Override
public String set(int index, String element) {
throw new UnsupportedOperationException("Not supported.");
}

@Override
public void add(int index, String element) {
throw new UnsupportedOperationException("Not supported.");
}

@Override
public String remove(int index) {
throw new UnsupportedOperationException("Not supported.");
}

@Override
public boolean addAll(int index, Collection c) {
throw new UnsupportedOperationException("Not supported.");
}

@Override
protected void removeRange(int fromIndex, int toIndex) {
throw new UnsupportedOperationException("Not supported.");
}

@Override
public List<String> subList(int fromIndex, int toIndex) {
throw new UnsupportedOperationException("Not supported.");
}

    static class Entry<T> implements Serializable{

String elementName;
boolean availableToAddLeftChildren;
boolean availableToAddRightChildren;
Entry parent, leftChild, rightChild;

public Entry(String elementName){
this.elementName = elementName;
availableToAddLeftChildren=true;
availableToAddRightChildren=true;
}

public boolean isAvailableToAddChildren(){
return availableToAddLeftChildren || availableToAddRightChildren;
}
}

}
        
        
        