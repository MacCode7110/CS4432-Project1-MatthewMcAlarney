import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BufferPool {
    private Frame[] buffers;

    /**
     * The index of the frame in the buffer pool that was last evicted
     */
    private int indexOfLastEvictedFrame;

    /**
     * Initializes a BufferPool using the size parameter, sets each frame to start off empty, and sets the index of the last evicted frame to -1
     * @param size the number of frames in the buffer pool
     */
    public void initialize(int size) {
        this.buffers = new Frame[size];
        for (int i = 0; i < this.buffers.length; i++) {
            this.buffers[i] = new Frame(-1);
        }
        this.indexOfLastEvictedFrame = -1;
    }

    /**
     * Obtains the relative file path to a desired block using the corresponding file/block number
     * @param blockId the block number of the desired block
     * @return the relative file path to the desired block
     */
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

    /**
     * Obtains the file number of the file that contains the record with the specified recordNumber
     * @param recordNumber the record number that the user inputs to the application
     * @return the file number of the file containing the record with the specified recordNumber
     */
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

    /**
     * Obtains the modified record number (a value falling in the range of 1 to 100) for the specified recordNumber
     * @param recordNumber the record number that the user inputs to the application
     * @return the modified record number, which is a number that falls in the range of 1 to 100; it is used to correctly access records within an array of bytes read from a block file
     */
    public int obtainModifiedRecordNumber(int recordNumber) {
        int modifiedRecordNumber = 0;

        if (recordNumber >= 1 && recordNumber <= 100) {
            modifiedRecordNumber = recordNumber;
        } else if (recordNumber >= 101 && recordNumber <= 200) {
            modifiedRecordNumber = recordNumber - 100;
        } else if (recordNumber >= 201 && recordNumber <= 300) {
            modifiedRecordNumber = recordNumber - 200;
        } else if (recordNumber >= 301 && recordNumber <= 400) {
            modifiedRecordNumber = recordNumber - 300;
        } else if (recordNumber >= 401 && recordNumber <= 500) {
            modifiedRecordNumber = recordNumber - 400;
        } else if (recordNumber >= 501 && recordNumber <= 600) {
            modifiedRecordNumber = recordNumber - 500;
        } else if (recordNumber >= 601 && recordNumber <= 700) {
            modifiedRecordNumber = recordNumber - 600;
        }

        return modifiedRecordNumber;
    }

    /**
     * Checks if the block with the specified blockId is currently occupying a frame in the buffer pool
     * @param blockId the block number of the block that this method searches for in the buffer pool
     * @return the index of the frame that contains the block with the specified blockId if the block is found; otherwise, return -1
     */
    public int blockAvailableInPool(int blockId) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockId() == blockId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Obtains the content of the block with the specified blockId
     * @param blockId the block number of the desired block
     * @return the content of the desired block
     */
    public String getBlockContent(int blockId) {
        int bufferIndex = blockAvailableInPool(blockId);
        if (bufferIndex != -1) {
            return new String(this.buffers[bufferIndex].getBlockContentArr());
        }
        return null;
    }

    /**
     * Gets the index of the frame in the buffer pool that was last evicted
     * @return the index of the frame in the buffer pool that was last evicted
     */
    public int getIndexOfLastEvictedFrame() {
        return indexOfLastEvictedFrame;
    }

    /**
     * Sets the index of the frame in the buffer pool that was last evicted
     * @param indexOfLastEvictedFrame the index of the frame in the buffer pool that was last evicted
     */
    public void setIndexOfLastEvictedFrame(int indexOfLastEvictedFrame) {
        this.indexOfLastEvictedFrame = indexOfLastEvictedFrame;
    }

    /**
     * Searches for the first frame in the buffer pool that is empty, which is the case if the blockId of the frame is equal to -1
     * @return the index of the first frame in the buffer pool that is empty; if no frames in the buffer pool are empty, then the method returns -1
     */
    public int searchForEmptyFrame() {
        for (int i = 0; i < this.buffers.length; i++) {
            if (this.buffers[i].getBlockId() == -1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Reads the block with the specified blockId from disk into the first empty frame found in the buffer pool; it is verified before calling this method that there is an empty frame in the buffer pool
     * @param blockId the block number of the desired block
     * @return the index of the frame now containing the block that was read from disk
     * @throws IOException
     */
    public int readBlockFromDisk(int blockId) throws IOException {
        int frameIndex = searchForEmptyFrame();
        String filePath;
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

    /**
     * Searches for the next frame in the buffer pool to evict
     * The search begins at the frame positioned immediately after the frame that was last evicted, and it iterates over each subsequent frame until the starting frame has been looped back to
     * @return the index of the next frame that can be evicted; if no frames can be evicted (all frames are pinned), then return -1
     */
    public int searchForNextFrameToEvict() {

        if (this.indexOfLastEvictedFrame == this.buffers.length - 1) {

            for (int i = 0; i < this.buffers.length; i++) {
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

            for (int i = 0; i <= (this.indexOfLastEvictedFrame + 1); i++) {
                if (!(this.buffers[i].isPinned())) {
                    return i;
                }
            }

        }

        return -1;
    }

    /**
     * Evict the frame in the buffer pool with the specified frameIndex
     * If the content of the frame with the specified frameIndex is dirty, then the bytes are written back to the appropriate file in disk
     * When a frame is evicted, the blockId set to -1, the dirty flag set to false, the pinned flag set to false, and the block content set to a new byte array instance
     * The index of the last evicted frame set to frameIndex
     * @param frameIndex the index of the frame in the buffer pool that is evicted
     * @return the file/block number corresponding to the block that is in the frame to be evicted
     * @throws IOException
     */
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

    /**
     * Prints a message detailing a failure to access the block with the specified blockId
     * @param blockId the block number of the desired block
     * @param attemptedWrite whether write to block content was attempted
     * @param attemptedPIN whether PIN of frame was attempted
     * @param attemptedUNPIN whether UNPIN of frame was attempted
     */
    public void printAccessFailure(int blockId, boolean attemptedWrite, boolean attemptedPIN, boolean attemptedUNPIN) {
        if (attemptedPIN) {
            System.out.println("The corresponding block " + blockId + " cannot be pinned because the memory buffers are full");
        } else if (attemptedUNPIN) {
            System.out.println("The corresponding block " + blockId + " cannot be unpinned because it is not in memory");
        } else {
            if (attemptedWrite) {
                System.out.println("The corresponding block " + blockId + " cannot be accessed from disk because the memory buffers are full; Write was unsuccessful");
            } else {
                System.out.println("The corresponding block " + blockId + " cannot be accessed from disk because the memory buffers are full");
            }
        }
    }

    /**
     * Prints a message detailing the outcome of a GET command
     * @param bufferIndex the index of frame in the buffer pool containing the desired block
     * @param modifiedRecordNumber the number of the desired record modified to fall in the range of 1 to 100
     * @param blockId the block number of the desired block
     * @param blockAlreadyInMemory whether the desired block was already in the buffer pool
     * @param frameEvicted whether a frame was evicted
     * @param evictedFileNumber the file/block number corresponding to the block in the evicted frame; -1 if no frame was evicted
     */
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

    /**
     * Prints a message detailing the outcome of a SET command
     * @param bufferIndex the index of frame in the buffer pool containing the desired block
     * @param blockId the block number of the desired block
     * @param blockAlreadyInMemory whether the desired block was already in the buffer pool
     * @param frameEvicted whether a frame was evicted
     * @param evictedFileNumber the file/block number corresponding to the block in the evicted frame; -1 if no frame was evicted
     */
    public void printSETResult(int bufferIndex, int blockId, boolean blockAlreadyInMemory, boolean frameEvicted, int evictedFileNumber) {
        int frameNumber = bufferIndex + 1;

        if (blockAlreadyInMemory) {
            System.out.println("Write was successful" + "; " + "File " + blockId + " already in memory; Located in Frame " + frameNumber);
        } else if (frameEvicted) {
            System.out.println("Write was successful" + "; " + "Brought file " + blockId + " from disk; Placed in Frame " + frameNumber + "; Evicted file " + evictedFileNumber + " from Frame " + frameNumber);
        } else {
            System.out.println("Write was successful" + "; " + "Brought file " + blockId + " from disk; Placed in Frame " + frameNumber);
        }

    }

    /**
     * Prints a message detailing the outcome of a PIN command
     * @param bufferIndex the index of frame in the buffer pool containing the desired block
     * @param blockId the block number of the desired block
     * @param blockAlreadyInMemory whether the desired block was already in the buffer pool
     * @param blockAlreadyPinned whether the desired block was already pinned before invoking the PIN command
     * @param frameEvicted whether a frame was evicted
     * @param evictedFileNumber the file/block number corresponding to the block in the evicted frame; -1 if no frame was evicted
     */
    public void printPINResult(int bufferIndex, int blockId, boolean blockAlreadyInMemory, boolean blockAlreadyPinned, boolean frameEvicted, int evictedFileNumber) {
        int frameNumber = bufferIndex + 1;

        if (blockAlreadyInMemory) {
            if (blockAlreadyPinned) {
                System.out.println("File " + blockId + " pinned in Frame " + frameNumber + "; Already pinned");
            } else {
                System.out.println("File " + blockId + " pinned in Frame " + frameNumber + "; Not already pinned");
            }
        } else if (frameEvicted) {
            System.out.println("File " + blockId + " pinned in Frame " + frameNumber + "; Not already pinned; " + "Evicted file " + evictedFileNumber + " from Frame " + frameNumber);
        } else {
            System.out.println("File " + blockId + " pinned in Frame " + frameNumber + "; Not already pinned; " + "Brought file " + blockId + " from disk; Placed in Frame " + frameNumber);
        }
    }

    /**
     * Prints a message detailing the outcome of an UNPIN command
     * @param bufferIndex the index of frame in the buffer pool containing the desired block
     * @param blockId the block number of the desired block
     * @param blockAlreadyUnpinned whether the desired block was already unpinned before invoking the UNPIN command
     */
    public void printUNPINResult(int bufferIndex, int blockId, boolean blockAlreadyUnpinned) {
        int frameNumber = bufferIndex + 1;

        if (blockAlreadyUnpinned) {
            System.out.println("File " + blockId + " in frame " + frameNumber + " is unpinned; Frame " + frameNumber + " was already unpinned");
        } else {
            System.out.println("File " + blockId + " is unpinned in frame " + frameNumber + "; Frame " + frameNumber + " was not already unpinned");
        }
    }

    /**
     * Processes a GET command for the desired record with the specified recordNumber
     * @param recordNumber the record number that the user inputs to the application
     * @throws IOException
     */
    public void GET(int recordNumber) throws IOException {
        int blockId = obtainFileNumber(recordNumber);
        int modifiedRecordNumber = obtainModifiedRecordNumber(recordNumber);
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
            printAccessFailure(blockId, false, false, false);
        }
    }

    /**
     * Processes a SET command for the desired record with the specified recordNumber
     * @param recordNumber the record number that the user inputs to the application
     * @param newContent the new content intended to overwrite the current content of the desired record
     * @throws IOException
     */
    public void SET(int recordNumber, String newContent) throws IOException {
        int blockId = obtainFileNumber(recordNumber);
        int modifiedRecordNumber = obtainModifiedRecordNumber(recordNumber);
        int bufferIndex = blockAvailableInPool(blockId);
        int evictedFileNumber;

        if (bufferIndex != -1) { //CASE 1
            this.buffers[bufferIndex].updateRecord(modifiedRecordNumber, newContent);
            this.buffers[bufferIndex].setDirty(true);
            printSETResult(bufferIndex, blockId, true, false, -1);
        } else if (searchForEmptyFrame() != -1) { //CASE 2
            bufferIndex = readBlockFromDisk(blockId);
            this.buffers[bufferIndex].updateRecord(modifiedRecordNumber, newContent);
            this.buffers[bufferIndex].setDirty(true);
            printSETResult(bufferIndex, blockId, false, false, -1);
        } else if (searchForNextFrameToEvict() != -1) { //CASE 3
            evictedFileNumber = evictFrame(searchForNextFrameToEvict());
            bufferIndex = readBlockFromDisk(blockId);
            this.buffers[bufferIndex].updateRecord(modifiedRecordNumber, newContent);
            this.buffers[bufferIndex].setDirty(true);
            printSETResult(bufferIndex, blockId, false, true, evictedFileNumber);
        } else { //CASE 4
            printAccessFailure(blockId, true, false, false);
        }
    }

    /**
     * Processes a PIN command for the desired block with the specified blockId
     * @param blockId the block number of the desired block
     * @throws IOException
     */
    public void PIN(int blockId) throws IOException {
        int bufferIndex = blockAvailableInPool(blockId);
        int evictedFileNumber;
        boolean blockAlreadyPinned = false;

        if (bufferIndex != -1) { //CASE 1
            if (this.buffers[bufferIndex].isPinned()) {
                blockAlreadyPinned = true;
            } else {
                this.buffers[bufferIndex].setPinned(true);
            }
            printPINResult(bufferIndex, blockId, true, blockAlreadyPinned, false, -1);
        } else if (searchForEmptyFrame() != -1) { //CASE 2
            bufferIndex = readBlockFromDisk(blockId);
            this.buffers[bufferIndex].setPinned(true);
            printPINResult(bufferIndex, blockId, false, false, false, -1);
        } else if (searchForNextFrameToEvict() != -1) { //CASE 3
            evictedFileNumber = evictFrame(searchForNextFrameToEvict());
            bufferIndex = readBlockFromDisk(blockId);
            this.buffers[bufferIndex].setPinned(true);
            printPINResult(bufferIndex, blockId, false, false, true, evictedFileNumber);
        } else { //CASE 4
            printAccessFailure(blockId, false, true, false);
        }
    }

    /**
     * Processes an UNPIN command for the desired block with the specified blockId
     * @param blockId the block number of the desired block
     */
    public void UNPIN(int blockId) {
        int bufferIndex = blockAvailableInPool(blockId);
        boolean blockAlreadyUnpinned = false;

        if (bufferIndex != -1) { //CASE 1
            if (!(this.buffers[bufferIndex].isPinned())) {
                blockAlreadyUnpinned = true;
            } else {
                this.buffers[bufferIndex].setPinned(false);
            }
            printUNPINResult(bufferIndex, blockId, blockAlreadyUnpinned);
        } else { //CASE 2
            printAccessFailure(blockId, false, false, true);
        }
    }
}
