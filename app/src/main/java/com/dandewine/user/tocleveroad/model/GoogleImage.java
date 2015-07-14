package com.dandewine.user.tocleveroad.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GoogleImage implements Parcelable {
    private String title;
    private String link;
    private String displayLink;
    private String mime;
    private Image image;
    private boolean isFavourite;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDisplayLink() {
        return displayLink;
    }

    public String getMime() {
        return mime;
    }

    public Image getImage() {
        return image;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public static class Image implements Parcelable {
        private long height;
        private long width;
        private long byteSize;
        private String thumbnailLink;
        private long thumbnailHeight;
        private long thumbnailWidth;

        public long getHeight() {
            return height;
        }

        public long getWidth() {
            return width;
        }

        public long getByteSize() {
            return byteSize;
        }

        public String getThumbnailLink() {
            return thumbnailLink;
        }

        public long getThumbnailHeight() {
            return thumbnailHeight;
        }

        public long getThumbnailWidth() {
            return thumbnailWidth;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.height);
            dest.writeLong(this.width);
            dest.writeLong(this.byteSize);
            dest.writeString(this.thumbnailLink);
            dest.writeLong(this.thumbnailHeight);
            dest.writeLong(this.thumbnailWidth);
        }

        public Image() { }

        private Image(Parcel in) {
            this.height = in.readLong();
            this.width = in.readLong();
            this.byteSize = in.readLong();
            this.thumbnailLink = in.readString();
            this.thumbnailHeight = in.readLong();
            this.thumbnailWidth = in.readLong();
        }

        public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
            public Image createFromParcel(Parcel source) {
                return new Image(source);
            }

            public Image[] newArray(int size) {
                return new Image[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.displayLink);
        dest.writeString(this.mime);
        dest.writeParcelable(this.image, 0);
    }

    public GoogleImage() {
    }

    private GoogleImage(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.displayLink = in.readString();
        this.mime = in.readString();
        this.image = in.readParcelable(Image.class.getClassLoader());
    }

    public static final Parcelable.Creator<GoogleImage> CREATOR = new Parcelable.Creator<GoogleImage>() {
        public GoogleImage createFromParcel(Parcel source) {
            return new GoogleImage(source);
        }

        public GoogleImage[] newArray(int size) {
            return new GoogleImage[size];
        }
    };

}