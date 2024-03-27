import java.util.Arrays;

public class Frame {
    private byte[] blockContentArr;
    private boolean dirty;
    private boolean pinned;
    private int blockId;

    /**
     * Constructs a Frame that holds a block with the specified blockId; if blockId is equal to -1, then the frame is empty
     * @param blockId the block number of the block in the frame
     */
    public Frame(int blockId) {
        this.dirty = false;
        this.pinned = false;
        this.blockId = blockId;
        this.blockContentArr = new byte[4000];
    }

    /**
     * Gets the content of the block in the frame
     * @return the content of the block in the frame
     */
    public byte[] getBlockContentArr() {
        return blockContentArr;
    }

    /**
     * Gets the value of the dirty field of the frame
     * @return the value of the dirty field of the frame
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Gets the value of the pinned field of the frame
     * @return the value of the pinned field of the frame
     */
    public boolean isPinned() {
        return pinned;
    }

    /**
     * Gets the value of the blockId field of the frame
     * @return the value of the blockId field of the frame
     */
    public int getBlockId() {
        return blockId;
    }

    /**
     * Sets the block content of the frame
     * @param blockContentArr the block content for the frame
     */
    public void setBlockContentArr(byte[] blockContentArr) {
        this.blockContentArr = blockContentArr;
    }

    /**
     * Sets the dirty field of the frame
     * @param dirty the dirty value for the frame
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Sets the pinned field of the frame
     * @param pinned the pinned value for the frame
     */
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    /**
     * Sets the blockId field of the frame
     * @param blockId the blockId value for the frame
     */
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    /**
     * Gets the desired record with the specified modifiedRecordNumber within the block of the frame
     * @param modifiedRecordNumber the number of the desired record modified to fall in the range of 1 to 100
     * @return the String representation of the desired record; if the record is not found, the method returns null
     */
    public String getRecord(int modifiedRecordNumber) {
        for (int i = 0; i < this.blockContentArr.length; i++) {
            if (i == (modifiedRecordNumber - 1) * 40) {
                return new String(Arrays.copyOfRange(this.blockContentArr, i, i + 40));
            }
        }
        return null;
    }

    /**
     * Updates the desired record with the specified modifiedRecordNumber within the block of the frame
     * @param modifiedRecordNumber the number of the desired record modified to fall in the range of 1 to 100
     * @param newContent The new content intended to overwrite the current content of the desired record
     */
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
