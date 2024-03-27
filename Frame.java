import java.util.Arrays;

public class Frame {
    //Holds one file (one disk block).
    private byte[] blockContentArr;
    private boolean dirty;
    private boolean pinned;
    private int blockId;

    public Frame(int blockId) {
        this.dirty = false;
        this.pinned = false;
        this.blockId = blockId;
        this.blockContentArr = new byte[4000];
    }

    public byte[] getBlockContentArr() {
        return blockContentArr;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isPinned() {
        return pinned;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockContentArr(byte[] blockContentArr) {
        this.blockContentArr = blockContentArr;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public String getRecord(int modifiedRecordNumber) {
        for (int i = 0; i < this.blockContentArr.length; i++) {
            if (i == (modifiedRecordNumber - 1) * 40) {
                return new String(Arrays.copyOfRange(this.blockContentArr, i, i + 40));
            }
        }
        return null;
    }

    public void updateRecord(int modifiedRecordNumber, String newContent) {
        int recordIndex = (modifiedRecordNumber - 1) * 40;
        byte[] contentToByteArr = newContent.getBytes();

        if (newContent.equals(new String(Arrays.copyOfRange(this.blockContentArr, recordIndex, recordIndex + 40)))) {
            setDirty(false);
        } else {
            setDirty(true);
        }

        for (int i = recordIndex; i < recordIndex + 40; i++) {
            this.blockContentArr[i] = contentToByteArr[i - ((modifiedRecordNumber - 1) * 40)];
        }
    }
}
