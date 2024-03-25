public class BufferPool {
    private Frame[] buffers;
    public BufferPool(int size) {
        this.buffers = new Frame[size];
        for (int i = 0; i < this.buffers.length; i++) {
            this.buffers[i] = new Frame(-1);
        }
    }
    public int blockAvailableInPool(int blockId) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockId() == blockId) {
                return i;
            }
        }
        return -1;
    }

    public String getBlockContent(int blockId) {
        int bufferIndex = blockAvailableInPool(blockId);
        if (bufferIndex != -1) {
            return new String(this.buffers[bufferIndex].getBlockContentArr());
        }
        return null;
    }

    public int searchForEmptyFrame() {
        for (int i = 0; i < this.buffers.length; i++) {
            if (this.buffers[i].getBlockId() == -1) {
                return i;
            }
        }
        return -1;
    }

    public void readBlockFromDisk(int blockId) {
        int firstEmptyFrameIndex = searchForEmptyFrame();
        if (firstEmptyFrameIndex != -1) {
            //Need to read block from disk
        }
    }

    public void selectAndEvictFrame() {

    }

    public void GET(int recordNumber) {
        //Need to modify ID sent to helper functions.
    }

    public void SET(int recordNumber, String content) {
        //Need to modify ID sent to helper functions.
    }

    public void PIN(int blockId) {

    }

    public void UNPIN(int blockId) {

    }
}
