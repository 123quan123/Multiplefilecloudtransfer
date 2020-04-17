 package com.mec.mfct.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import com.mec.mfct.section.FileSectionInfo;

/**
 * 
 * <ol>
 * 功能:资源的表示类
 * <li>要区分一个资源的名称name以及标识符id</li>
 * <li>绝对路径absoluteRoot可以由请求端自行设置</li>
 * <li>版本version用来区分资源版本</li>
 * <li>rsiList为相对路径的封装</li>
 * <li>fsiList为传输中单个资源封装类</li>
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public class ResourceBaseInfo {
    private static final String RESOURCE_XML = "F:\\.mecResource\\resource.xml";
    
    private String name;
    private int id;
    private String absoluteRoot;
    private long version;
    private List<ResourceStructInfo> rsiList;
    private List<FileSectionInfo> fsiList;
    private long totalSize;
    
    private XMLEditor editor;

    public ResourceBaseInfo() {
    }
    
    public ResourceBaseInfo(ResourceBaseInfo rbi) {
        this.name = rbi.name;
        this.id = rbi.id;
        this.version = rbi.version;
        this.absoluteRoot = rbi.absoluteRoot;
        this.fsiList = rbi.fsiList;
        this.rsiList = rbi.rsiList;
    }
    
    public void saveResource(){
        if (editor == null) {
            createXmlEditor(); 
        }
        List<ResourceStructInfo> tmprsiList = rsiList;
        List<FileSectionInfo> tmpsiList = fsiList;
        
        rsiList = null;
        fsiList = null;
        
        editor.insert(RESOURCE_XML, this);
        rsiList = tmprsiList;
        fsiList = tmpsiList;
    }
    
    private void createXmlEditor() {
        try {
            editor = new XMLEditor();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        }
    }
    
    public String getName() {
        return name;
    }
    
    public ResourceBaseInfo setName(String name) {
        this.name = name;
        return this;
    }
    
    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getId() {
        return id;
    }
    
    public ResourceBaseInfo setId(int id) {
        this.id = id;
        return this;
    }
    
    public String getAbsoluteRoot() {
        return absoluteRoot;
    }
    
    public ResourceBaseInfo setAbsoluteRoot(String absoluteRoot) {
        this.absoluteRoot = absoluteRoot;
        return this;
    }
    
    public long getVersion() {
        return version;
    }
    
    public ResourceBaseInfo setVersion(long version) {
        this.version = version;
        return this;
    }
    
    public List<ResourceStructInfo> getRsiList() {
        return rsiList;
    }
    
    public ResourceBaseInfo setRsiList(List<ResourceStructInfo> rsiList) {
        this.rsiList = rsiList;
        return this;
    }
    

    public List<FileSectionInfo> getFsiList() {
        return fsiList;
    }
    
    public void setFsiList(List<FileSectionInfo> fsiList) {
        this.fsiList = fsiList;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int)(version ^ (version >>> 32));
        return result;
    }
    
    public void exploreResource() {
        exploreResource(null);
    }
    
    public void exploreResource(String root) {
        root = (root == null ? this.absoluteRoot : root);
        File file = new File(root);
        //扫描并构成资源结构信息。
        rsiList = new ArrayList<ResourceStructInfo>();
        scanResourceRoot(rsiList, root, file, 1);
    }
    
    
    private int scanResourceRoot(List<ResourceStructInfo> rsiList, String absolutePath, File file, int fristFilehandle){
        if (file.isFile()) {
            return createResourceStructInfo(rsiList, absolutePath, file, fristFilehandle);
        }
        File[] fileList = file.listFiles();
        for (File f : fileList) {
            if (f.isDirectory()) {
                fristFilehandle = scanResourceRoot(rsiList, absolutePath, f, fristFilehandle);
            } else {
                fristFilehandle = createResourceStructInfo(rsiList, absolutePath, f, fristFilehandle);
            }
        }
        return fristFilehandle;
    }
    
    private int createResourceStructInfo(List<ResourceStructInfo> rsiList, String curfileName, File curFile, int fileHandle) {
        ResourceStructInfo rsi = new ResourceStructInfo();
        rsi.setFileHandle(fileHandle);
        rsi.setFilePath(curFile.getAbsolutePath().replace(curfileName, ""));
        long size = curFile.length();
        rsi.setFsize(size);
        rsiList.add(rsi);
        if (fsiList == null) {
            fsiList = new ArrayList<FileSectionInfo>();
        }
        FileSectionInfo fileSectionInfo = new FileSectionInfo();
        fileSectionInfo.setFileHandle(fileHandle);
        fileSectionInfo.setOffset(0);
        fileSectionInfo.setSize((int)size);
        fsiList.add(fileSectionInfo);
        fileHandle++;
        return fileHandle;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceBaseInfo other = (ResourceBaseInfo)obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("AppName=").append(name).append('\n')
            .append("id=").append(id).append('\n')
            .append("version=").append(version).append('\n')
            .append("absoluteRoot=").append(absoluteRoot);
        
        if (rsiList != null) {
            result.append("\nStruct-List:");
            for (ResourceStructInfo rsi : rsiList) {
                result.append("\n\t").append(rsi);
            }
        }
        
        if (fsiList != null) {
            result.append("\nRequest-List:");
            for (FileSectionInfo si : fsiList) {
                result.append("\n\t").append(si);
            }
        }
        
        return result.toString();
    }
    
}
