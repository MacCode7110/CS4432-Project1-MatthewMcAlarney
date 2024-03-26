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
        String filePath = "";
        switch (blockId) {
            case 1:
                filePath = "Project1/F1.txt";
                break;
            case 2:
                filePath = "Project1/F2.txt";
                break;
            case 3:
                filePath = "Project1/F3.txt";
                break;
            case 4:
                filePath = "Project1/F4.txt";
                break;
            case 5:
                filePath = "Project1/F5.txt";
                break;
            case 6:
                filePath = "Project1/F6.txt";
                break;
            case 7:
                filePath = "Project1/F7.txt";
                break;
        }
        return filePath;
    }

    public int obtainFileNumber(int recordNumber) {
        int fileNumber = 0;
        if (recordNumber >= 0 && recordNumber <= 99) {
            fileNumber = 1;
        } else if (recordNumber >= 100 && recordNumber <= 199) {
            fileNumber = 2;
        } else if (recordNumber >= 200 && recordNumber <= 299) {
            fileNumber = 3;
        } else if (recordNumber >= 300 && recordNumber <= 399) {
            fileNumber = 4;
        } else if (recordNumber >= 400 && recordNumber <= 499) {
            fileNumber = 5;
        } else if (recordNumber >= 500 && recordNumber <= 599) {
            fileNumber = 6;
        } else if (recordNumber >= 600 && recordNumber <= 699) {
            fileNumber = 7;
        }
        return fileNumber;
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
        int blockId = obtainFileNumber(recordNumber);
        int bufferIndex = blockAvailableInPool(blockId);
        //Continue here
    }

    public void SET(int recordNumber, String content) {
        //Need to modify ID sent to helper functions.
    }

    public void PIN(int blockId) {

    }

    public void UNPIN(int blockId) {

    }
}
