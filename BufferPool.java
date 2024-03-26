import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BufferPool {
    private Frame[] buffers;
    private int indexOfLastEvictedFrame;
    public void initialize(int size) {
        this.buffers = new Frame[size];
        for (int i = 0; i < this.buffers.length; i++) {
            this.buffers[i] = new Frame(-1);
        }
        this.indexOfLastEvictedFrame = -1;
    }

    public String obtainFilePath(int blockId) {
        if (blockId == 1) {
            return "Project1/F1.txt";
        } else if (blockId == 2) {
            return "Project1/F2.txt";
        } else if (blockId == 3) {
            return "Project1/F3.txt";
        } else if (blockId == 4) {
            return "Project1/F4.txt";
        } else if (blockId == 5) {
            return "Project1/F5.txt";
        } else if (blockId == 6) {
            return "Project1/F6.txt";
        } else if (blockId == 7) {
            return "Project1/F7.txt";
        }
        return null;
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

    public int getIndexOfLastEvictedFrame() {
        return indexOfLastEvictedFrame;
    }

    public void setIndexOfLastEvictedFrame(int indexOfLastEvictedFrame) {
        this.indexOfLastEvictedFrame = indexOfLastEvictedFrame;
    }

    public int searchForEmptyFrame() {
        for (int i = 0; i < this.buffers.length; i++) {
            if (this.buffers[i].getBlockId() == -1) {
                return i;
            }
        }
        return -1;
    }

    public void readBlockFromDisk(int blockId) throws IOException {
        int firstEmptyFrameIndex = searchForEmptyFrame();
        String filePath = "";
        Path path;
        byte[] block;

        if (firstEmptyFrameIndex != -1) {
            filePath = obtainFilePath(blockId);
            path = Paths.get(filePath);
            block = Files.readAllBytes(path);
            this.buffers[firstEmptyFrameIndex].setBlockContentArr(block);
        }
    }

    public void evictFrame(int frameIndex) throws IOException {
        String filePath = "";
        Path path;

        if(this.buffers[frameIndex].isDirty()) {
            filePath = obtainFilePath(this.buffers[frameIndex].getBlockId());
            path = Paths.get(filePath);
            Files.write(path, this.buffers[frameIndex].getBlockContentArr());
        }

        this.buffers[frameIndex].setBlockId(-1);
        this.buffers[frameIndex].setPinned(false);
        this.buffers[frameIndex].setBlockContentArr(new byte[4000]);

        setIndexOfLastEvictedFrame(frameIndex);
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
