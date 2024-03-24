public class BufferPool {
    private Frame[] buffers;

    public BufferPool(int size) {
        this.buffers = new Frame[size];
        for (int i = 0; i < this.buffers.length; i++) {
            this.buffers[i] = new Frame(-1);
        }
    }
    public int blockAvailableInPool(int blockId) {
        return -1;
    }

    public String getBlockContent(int blockId) {
        if (blockAvailableInPool(blockId) != -1) {
            return "content";
        }
        return "no content";
    }

    public void readBlockFromDisk() {

    }

    public int searchForEmptyFrame() {
        return 0;
    }

    public void selectAndEvictFrame() {

    }
}
