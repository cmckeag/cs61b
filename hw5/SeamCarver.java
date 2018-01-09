import java.util.PriorityQueue;
import java.awt.Color;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture pic;
    private int picHeight;
    private int picWidth;
    private double[][] energyArray;

    public SeamCarver(Picture picture) {
        this.pic = new Picture(picture);
        picHeight = picture.height();
        picWidth = picture.width();
        energyArray = new double[picWidth][picHeight];
        int heightIndex = 0;
        int widthIndex = 0;
        while (widthIndex < picWidth) {
            heightIndex = 0;
            while (heightIndex < picHeight) {
                energyArray[widthIndex][heightIndex] = energy(widthIndex, heightIndex);
                heightIndex += 1;
            }
            widthIndex += 1;
        }
    }

    private class Path implements Comparable<Path> {
        private int col;
        private int row;
        private double energy;
        private Path previous;

        public Path(int col, int row, Path prior) {
            this.col = col;
            this.row = row;
            this.previous = prior;
            energy = energyArray[col][row];
            if (prior != null) {
                energy += prior.energy;
            }
        }

        public int compareTo(Path other) {
            if (this.energy > other.energy) {
                return 1;
            } else if (this.energy < other.energy) {
                return -1;
            } else {
                return 0;
            }
        }

        public int column() {
            return this.col;
        }

        public int row() {
            return this.row;
        }

        public Path previous() {
            return this.previous;
        }
    }

    public int width() {
        return picWidth;
    }

    public int height() {
        return picHeight;
    }

    public double energy(int col, int row) {
        if (col < 0 || row < 0 || col >= picWidth || row >= picHeight) {
            throw new java.lang.IndexOutOfBoundsException("Invalid arguments");
        }
        int above = row - 1;
        if (above < 0) {
            above += picHeight;
        }
        int below = (row + 1) % picHeight;
        int left = col - 1;
        if (left < 0) {
            left += picWidth;
        }
        int right = (col + 1) % picWidth;
        Color leftColor = pic.get(left, row);
        Color rightColor = pic.get(right, row);
        Color aboveColor = pic.get(col, above);
        Color belowColor = pic.get(col, below);
        double deltaX = (Math.pow(leftColor.getRed() - rightColor.getRed(), 2) 
            + Math.pow(leftColor.getGreen() - rightColor.getGreen(), 2) 
            + Math.pow(leftColor.getBlue() - rightColor.getBlue(), 2));
        double deltaY = (Math.pow(aboveColor.getRed() - belowColor.getRed(), 2) 
            + Math.pow(aboveColor.getGreen() - belowColor.getGreen(), 2) 
            + Math.pow(aboveColor.getBlue() - belowColor.getBlue(), 2));
        return deltaX + deltaY;
    }

    public int[] findVerticalSeam() {
        int[] seam = new int[picHeight];
        if (picWidth < 2) {
            return seam;
        }
        Path[][] paths = new Path[picWidth][picHeight];
        int ind = 0;
        while (ind < picWidth) {
            paths[ind][picHeight - 1] = new Path(ind, picHeight - 1, null);
            ind += 1;
        }
        int heightIndex = picHeight - 2;
        int widthIndex = 0;
        while (heightIndex >= 0) {
            widthIndex = 0;
            while (widthIndex < picWidth) {
                PriorityQueue<Path> priors = new PriorityQueue<Path>();
                priors.add(paths[widthIndex][heightIndex + 1]);
                if (widthIndex == 0) {
                    priors.add(paths[widthIndex + 1][heightIndex + 1]);
                } else if (widthIndex == picWidth - 1) {
                    priors.add(paths[widthIndex - 1][heightIndex + 1]);
                } else {
                    priors.add(paths[widthIndex + 1][heightIndex + 1]);
                    priors.add(paths[widthIndex - 1][heightIndex + 1]);
                }
                paths[widthIndex][heightIndex] = new Path(widthIndex, heightIndex, priors.poll());
                widthIndex += 1;
            }
            heightIndex -= 1;
        }

        PriorityQueue<Path> finals = new PriorityQueue<Path>();
        widthIndex = 0;
        while (widthIndex < picWidth) {
            finals.add(paths[widthIndex][0]);
            widthIndex += 1;
        }
        Path bestPath = finals.poll();
        int index = 0;
        while (index < picHeight) {
            seam[index] = bestPath.column();
            bestPath = bestPath.previous();
            index += 1;
        }

        return seam;
    }

    public int[] findHorizontalSeam() {
        invert();
        int[] result = findVerticalSeam();
        invert();
        return result;
    }

    public void removeVerticalSeam(int[] seam) {
        if (picWidth < 2) {
            throw new IllegalArgumentException("Picture is too small to carve");
        }
        if (seam.length != picHeight) {
            throw new IllegalArgumentException("Invalid seam: incorrect length");
        }
        Picture newPic = new Picture(picWidth - 1, picHeight);
        int widthIndex = 0;
        int heightIndex = 0;
        while (heightIndex < picHeight) {
            widthIndex = 0;
            while (widthIndex < (picWidth - 1)) {
                if (widthIndex != seam[heightIndex]) {
                    newPic.set(widthIndex, heightIndex, pic.get(widthIndex, heightIndex));
                } else {
                    if (heightIndex > 0 
                        && Math.abs(seam[heightIndex] - seam[heightIndex - 1]) > 1) {
                        throw new IllegalArgumentException("Invalid seam: invalid path");
                    }
                }
                widthIndex += 1;
            }
            heightIndex += 1;
        }
        this.pic = new Picture(newPic);
        this.picWidth -= 1;
        widthIndex = 0;
        heightIndex = 0;
        while (widthIndex < picWidth) {
            heightIndex = 0;
            while (heightIndex < picHeight) {
                energyArray[widthIndex][heightIndex] = energy(
                    widthIndex, heightIndex);
                heightIndex += 1;
            }
            widthIndex += 1;
        }
    }

    public void removeHorizontalSeam(int[] seam) {
        if (picHeight < 2) {
            throw new IllegalArgumentException("Picture is too small to carve.");
        }
        if (seam.length != picWidth) {
            throw new IllegalArgumentException("Invalid seam: incorrect length");
        }
        Picture newPic = new Picture(picWidth, picHeight - 1);
        int widthIndex = 0;
        int heightIndex = 0;
        while (widthIndex < picWidth) {
            heightIndex = 0;
            while (heightIndex < (picHeight - 1)) {
                if (heightIndex != seam[widthIndex]) {
                    newPic.set(widthIndex, heightIndex, 
                        pic.get(widthIndex, heightIndex));
                } else {
                    if (widthIndex > 0 && Math.abs(seam[widthIndex] 
                        - seam[widthIndex - 1]) > 1) {
                        throw new IllegalArgumentException("Invalid seam: invalid path");
                    }
                }
                heightIndex += 1;
            }
            widthIndex += 1;
        }
        this.pic = new Picture(newPic);
        this.picHeight -= 1;
        widthIndex = 0;
        heightIndex = 0;
        while (widthIndex < picWidth) {
            heightIndex = 0;
            while (heightIndex < picHeight) {
                energyArray[widthIndex][heightIndex] = energy(widthIndex, heightIndex);
                heightIndex += 1;
            }
            widthIndex += 1;
        }
    }

    public Picture picture() {
        return new Picture(this.pic);
    }

    private void invert() {
        double[][] newEnergyArray = new double[picHeight][picWidth];
        int widthIndex = 0;
        int heightIndex = 0;
        while (widthIndex < picWidth) {
            heightIndex = 0;
            while (heightIndex < picHeight) {
                newEnergyArray[heightIndex][widthIndex] = energyArray[widthIndex][heightIndex];
                heightIndex += 1;
            }
            widthIndex += 1;
        }
        this.energyArray = newEnergyArray;
        int tempWidth = this.picWidth;
        this.picWidth = picHeight;
        this.picHeight = tempWidth;
    }
}
