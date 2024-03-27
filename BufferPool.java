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

    public int obtainFileNumber(int rawRecordNumber) {
        int fileNumber = 0;
        if (rawRecordNumber >= 0 && rawRecordNumber <= 99) {
            fileNumber = 1;
        } else if (rawRecordNumber >= 100 && rawRecordNumber <= 199) {
            fileNumber = 2;
        } else if (rawRecordNumber >= 200 && rawRecordNumber <= 299) {
            fileNumber = 3;
        } else if (rawRecordNumber >= 300 && rawRecordNumber <= 399) {
            fileNumber = 4;
        } else if (rawRecordNumber >= 400 && rawRecordNumber <= 499) {
            fileNumber = 5;
        } else if (rawRecordNumber >= 500 && rawRecordNumber <= 599) {
            fileNumber = 6;
        } else if (rawRecordNumber >= 600 && rawRecordNumber <= 699) {
            fileNumber = 7;
        }
        return fileNumber;
    }

    public int obtainModifiedRecordNumber(int rawRecordNumber) {
        int modifiedRecordNumber = 0;

        if (rawRecordNumber >= 1 && rawRecordNumber <= 100) {
            modifiedRecordNumber = rawRecordNumber;
        } else if (rawRecordNumber >= 101 && rawRecordNumber <= 200) {
            modifiedRecordNumber = rawRecordNumber - 100;
        } else if (rawRecordNumber >= 201 && rawRecordNumber <= 300) {
            modifiedRecordNumber = rawRecordNumber - 200;
        } else if (rawRecordNumber >= 301 && rawRecordNumber <= 400) {
            modifiedRecordNumber = rawRecordNumber - 300;
        } else if (rawRecordNumber >= 401 && rawRecordNumber <= 500) {
            modifiedRecordNumber = rawRecordNumber - 400;
        } else if (rawRecordNumber >= 501 && rawRecordNumber <= 600) {
            modifiedRecordNumber = rawRecordNumber - 500;
        } else if (rawRecordNumber >= 601 && rawRecordNumber <= 700) {
            modifiedRecordNumber = rawRecordNumber - 600;
        }

        return modifiedRecordNumber;
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

    public int readBlockFromDisk(int blockId) throws IOException {
        int frameIndex = searchForEmptyFrame();
        String filePath = "";
        Path path;
        byte[] block;

        filePath = obtainFilePath(blockId);
        path = Paths.get(filePath);
        block = Files.readAllBytes(path);
        this.buffers[frameIndex].setBlockId(blockId);
        this.buffers[frameIndex].setBlockContentArr(block);
        this.buffers[frameIndex].setDirty(false);
        this.buffers[frameIndex].setPinned(false);

        return frameIndex;
    }

    public int searchForNextFrameToEvict() {

        if (this.indexOfLastEvictedFrame == this.buffers.length - 1) {

            for (int i = 0; i < this.buffers.length - 1; i++) {
                if (!(this.buffers[i].isPinned())) {
                    return i;
                }
            }

        } else {

            for (int i = (this.indexOfLastEvictedFrame + 1); i < this.buffers.length; i++) {
                if (!(this.buffers[i].isPinned())) {
                    return i;
                }
            }

            for (int i = 0; i < (this.indexOfLastEvictedFrame + 1); i++) {
                if (!(this.buffers[i].isPinned())) {
                    return i;
                }
            }

        }

        return -1;
    }

    public int evictFrame(int frameIndex) throws IOException {
        String filePath;
        Path path;
        int evictedFileNumber = this.buffers[frameIndex].getBlockId();

        if (this.buffers[frameIndex].isDirty()) {
            filePath = obtainFilePath(this.buffers[frameIndex].getBlockId());
            path = Paths.get(filePath);
            Files.write(path, this.buffers[frameIndex].getBlockContentArr());
        }

        this.buffers[frameIndex].setBlockId(-1);
        this.buffers[frameIndex].setDirty(false);
        this.buffers[frameIndex].setPinned(false);
        this.buffers[frameIndex].setBlockContentArr(new byte[4000]);
        setIndexOfLastEvictedFrame(frameIndex);

        return evictedFileNumber;
    }

    public void printAccessFailure(int blockId, boolean attemptedWrite) {
        if (attemptedWrite) {
            System.out.println("The corresponding block " + blockId + " cannot be accessed from disk because the memory buffers are full; Write was unsuccessful");
        } else {
            System.out.println("The corresponding block " + blockId + " cannot be accessed from disk because the memory buffers are full");
        }
    }
    public void printGETResult(int bufferIndex, int modifiedRecordNumber, int blockId, boolean blockAlreadyInMemory, boolean frameEvicted, int evictedFileNumber) {
        String desiredRecord;
        int frameNumber = bufferIndex + 1;

        if (blockAlreadyInMemory) {
            desiredRecord = this.buffers[bufferIndex].getRecord(modifiedRecordNumber);
            System.out.println(desiredRecord + "; " + "File " + blockId + " already in memory; Located in Frame " + frameNumber);
        } else if (frameEvicted) {
            desiredRecord = this.buffers[bufferIndex].getRecord(modifiedRecordNumber);
            System.out.println(desiredRecord + "; " + "Brought file " + blockId + " from disk; Placed in Frame " + frameNumber + "; Evicted file " + evictedFileNumber + " from Frame " + frameNumber);
        } else {
            desiredRecord = this.buffers[bufferIndex].getRecord(modifiedRecordNumber);
            System.out.println(desiredRecord + "; " + "Brought file " + blockId + " from disk; Placed in Frame " + frameNumber);
        }

    }

    public void printSETResult(int bufferIndex, int modifiedRecordNumber, int blockId, boolean blockAlreadyInMemory, boolean frameEvicted, int evictedFileNumber) {
        int frameNumber = bufferIndex + 1;

        if (blockAlreadyInMemory) {
            System.out.println("Write was successful" + "; " + "File " + blockId + " already in memory; Located in Frame " + frameNumber);
        } else if (frameEvicted) {
            System.out.println("Write was successful" + "; " + "Brought file " + blockId + " from disk; Placed in Frame " + frameNumber + "; Evicted file " + evictedFileNumber + " from Frame " + frameNumber);
        } else {
            System.out.println("Write was successful" + "; " + "Brought file " + blockId + " from disk; Placed in Frame " + frameNumber);
        }

    }

    public void GET(int rawRecordNumber) throws IOException {
        int blockId = obtainFileNumber(rawRecordNumber);
        int modifiedRecordNumber = obtainModifiedRecordNumber(rawRecordNumber);
        int bufferIndex = blockAvailableInPool(blockId);
        int evictedFileNumber;

        if (bufferIndex != -1) { //CASE 1
            printGETResult(bufferIndex, modifiedRecordNumber, blockId, true, false, -1);
        } else if (searchForEmptyFrame() != -1) { //CASE 2
            printGETResult(readBlockFromDisk(blockId), modifiedRecordNumber, blockId, false, false, -1);
        } else if (searchForNextFrameToEvict() != -1) { //CASE 3
            evictedFileNumber = evictFrame(searchForNextFrameToEvict());
            printGETResult(readBlockFromDisk(blockId), modifiedRecordNumber, blockId, false, true, evictedFileNumber);
        } else { //CASE 4
            printAccessFailure(blockId, false);
        }
    }

    public void SET(int rawRecordNumber, String newContent) throws IOException {
        int blockId = obtainFileNumber(rawRecordNumber);
        int modifiedRecordNumber = obtainModifiedRecordNumber(rawRecordNumber);
        int bufferIndex = blockAvailableInPool(blockId);
        int evictedFileNumber;

        if (bufferIndex != -1) { //CASE 1
            this.buffers[bufferIndex].updateRecord(modifiedRecordNumber, newContent);
            this.buffers[bufferIndex].setDirty(true);
            printSETResult(bufferIndex, modifiedRecordNumber, blockId, true, false, -1);
        } else if (searchForEmptyFrame() != -1) { //CASE 2
            bufferIndex = readBlockFromDisk(blockId);
            this.buffers[bufferIndex].updateRecord(modifiedRecordNumber, newContent);
            this.buffers[bufferIndex].setDirty(true);
            printSETResult(bufferIndex, modifiedRecordNumber, blockId, false, false, -1);
        } else if (searchForNextFrameToEvict() != -1) { //CASE 3
            evictedFileNumber = evictFrame(searchForNextFrameToEvict());
            bufferIndex = readBlockFromDisk(blockId);
            this.buffers[bufferIndex].updateRecord(modifiedRecordNumber, newContent);
            this.buffers[bufferIndex].setDirty(true);
            printSETResult(bufferIndex, modifiedRecordNumber, blockId, false, true, evictedFileNumber);
        } else { //CASE 4
            printAccessFailure(blockId, true);
        }
    }

    public void PIN(int blockId) {

    }

    public void UNPIN(int blockId) {

    }
}
