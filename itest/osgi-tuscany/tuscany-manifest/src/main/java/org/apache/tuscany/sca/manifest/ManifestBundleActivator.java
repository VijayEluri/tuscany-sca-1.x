package org.apache.tuscany.sca.manifest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi bundle activator, which is run when Tuscany is run inside an OSGi runtime.
 *
 */
public class ManifestBundleActivator implements BundleActivator {
    
    private static final String TUSCANY_MANIFEST = "tuscany-sca-manifest.jar";
    private static final String TUSCANY_OSGI_MANIFEST_DIR = "org/apache/tuscany/sca/manifest";
    
    private ArrayList<Bundle> virtualBundles = new ArrayList<Bundle>();
    
    private static final String[] immutableJars = {
        "bcprov"
    };

	public void start(BundleContext bundleContext) throws Exception {
		
        install3rdPartyJarsIntoOSGi(bundleContext);
	}

	public void stop(BundleContext bundleContext) throws Exception {
        
        for (Bundle virtualBundle : virtualBundles) {
            try {
                virtualBundle.uninstall();
            } catch (Exception e) {
                // Ignore error
            }
        }
	}
	
    private void install3rdPartyJarsIntoOSGi(BundleContext bundleContext) {
    
        try {
            Bundle[] installedBundles = bundleContext.getBundles();
            HashSet<String> installedBundleSet = new HashSet<String>();
            for (Bundle bundle : installedBundles) {
                if (bundle.getSymbolicName() != null)
                    installedBundleSet.add(bundle.getSymbolicName());
            }
            
            // FIXME: SDO bundles dont have the correct dependencies
            System.setProperty("commonj.sdo.impl.HelperProvider", "org.apache.tuscany.sdo.helper.HelperProviderImpl");
            
            HashMap<String, InputStream> thirdPartyJarsWithManifests = new HashMap<String, InputStream>();
            HashSet<String> thirdPartyJars = new HashSet<String>();
            
            File tuscanyInstallDir = find3rdPartyJars(bundleContext, thirdPartyJars, thirdPartyJarsWithManifests);
            
//            Install 3rd party libs as a single virtual bundle            
//            String thirdPartyBundleLocation = tuscanyInstallDir.toURI().toURL().toString() + "/tuscany-3rdparty.jar";
//            
//            InputStream manifestStream = this.getClass().getClassLoader().getResourceAsStream(TUSCANY_OSGI_MANIFEST_DIR + "/MANIFEST.MF");
//            
//            if (manifestStream != null) {
//            
//                createAndInstallBundle(bundleContext, thirdPartyBundleLocation, manifestStream, tuscanyInstallDir, thirdPartyJars);
//            }
            
            for (String bundleName : thirdPartyJarsWithManifests.keySet()) {
                                
                String bundleLocation = tuscanyInstallDir.toURI().toURL().toString() + "/" + bundleName;
                InputStream bundleManifestStream = thirdPartyJarsWithManifests.get(bundleName);
                HashSet<String> jarSet = new HashSet<String>();
                jarSet.add(bundleName);
                
                createAndInstallBundle(bundleContext, bundleLocation, bundleManifestStream, tuscanyInstallDir, jarSet);
                bundleManifestStream.close();
                
            }
            
            for (String bundleName : thirdPartyJars) {
                
                if (bundleName.startsWith("org.apache.felix"))
                    continue;
                
                String bundleSymbolicName = "org.apache.tuscany.sca.3rdparty." + bundleName;
                if (bundleSymbolicName.endsWith(".jar")) bundleSymbolicName = bundleSymbolicName.substring(0, bundleSymbolicName.length()-4);
                if (installedBundleSet.contains(bundleSymbolicName))
                    continue;
                
                String bundleLocation = tuscanyInstallDir.toURI().toURL().toString() + "/" + bundleName;
                InputStream bundleManifestStream = createBundleManifest(tuscanyInstallDir, bundleName, bundleSymbolicName);
                HashSet<String> jarSet = new HashSet<String>();
                jarSet.add(bundleName);
                
                createAndInstallBundle(bundleContext, bundleLocation, bundleManifestStream, tuscanyInstallDir, jarSet);
                bundleManifestStream.close();
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    private File find3rdPartyJars(BundleContext bundleContext, 
            HashSet<String> thirdPartyJars, 
            HashMap<String, InputStream> thirdPartyBundleManifests) 
    throws IOException
    {
        
        Bundle manifestBundle = bundleContext.getBundle();
        
        String tuscanyDirName;
        File tuscanyInstallDir = null;
        String location = manifestBundle.getLocation();
        
        if ((tuscanyDirName = System.getenv("TUSCANY_HOME")) != null) {
            tuscanyInstallDir = new File(tuscanyDirName);
            if (!tuscanyInstallDir.exists() || !tuscanyInstallDir.isDirectory())
                tuscanyInstallDir = null;
        }
        if (tuscanyInstallDir == null && System.getProperty("TUSCANY_HOME") != null) {
            tuscanyInstallDir = new File(tuscanyDirName);
            if (!tuscanyInstallDir.exists() || !tuscanyInstallDir.isDirectory())
                tuscanyInstallDir = null;
        }
            
        if (tuscanyInstallDir == null && location != null && location.startsWith("file:") && location.endsWith(TUSCANY_MANIFEST)) {
            tuscanyDirName = location.substring(5, location.length()-TUSCANY_MANIFEST.length()); // strip "file:" and manifest jar name
            tuscanyInstallDir = new File(tuscanyDirName);
            if (!tuscanyInstallDir.exists() || !tuscanyInstallDir.isDirectory())
                tuscanyInstallDir = null;
        }
        if (tuscanyInstallDir == null) {
            System.out.println("TUSCANY_HOME not set, could not locate Tuscany install directory");
            return null;
        }
            
        String classPath = (String)manifestBundle.getHeaders().get("Class-Path");
        String[] classPathEntries = classPath.split(" ");
        for (String classPathEntry : classPathEntries) {
            classPathEntry = classPathEntry.trim();
            if (!classPathEntry.startsWith("tuscany") || classPathEntry.startsWith("tuscany-sdo") || classPathEntry.startsWith("tuscany-das")) {
                if (classPathEntry.endsWith(".jar")) {
                    String manifestName = TUSCANY_OSGI_MANIFEST_DIR + "/" + classPathEntry.substring(0, classPathEntry.length()-4) + ".mf";
                    InputStream manifestStream;
                    if ((manifestStream = this.getClass().getClassLoader().getResourceAsStream(manifestName)) != null)
                        thirdPartyBundleManifests.put(classPathEntry, manifestStream);
                    else
                        thirdPartyJars.add(classPathEntry);
                }
            }
        }
        
        return tuscanyInstallDir;
        
    }
    
    public Bundle createAndInstallBundle(BundleContext bundleContext, 
            String bundleLocation, 
            InputStream manifestStream,
            File tuscanyDir,
            final HashSet<String> thirdPartyJars) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Manifest manifest = new Manifest();
        manifest.read(manifestStream);
        
        StringBuilder bundleClassPath = new StringBuilder(".");
        for (String jar : thirdPartyJars) {
            bundleClassPath.append(',');
            bundleClassPath.append(jar);           
        }
        
        if (thirdPartyJars.size() > 1)
            manifest.getMainAttributes().putValue("Bundle-ClassPath", bundleClassPath.toString());

        JarOutputStream jarOut = new JarOutputStream(out, manifest);
        
        File[] jars = tuscanyDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return thirdPartyJars.contains(name);
            }
            
        });
        

        String classpath = manifest.getMainAttributes().getValue("Bundle-ClassPath");
        boolean embed = classpath != null && !classpath.trim().equals(".");
        for (File jarFile : jars) {
            if (embed)
                addFileToJar(jarFile, jarOut);
            else {
                copyJar(jarFile, jarOut);
            }
        }

        jarOut.close();
        out.close();
        
        ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());
        return bundleContext.installBundle(bundleLocation, inStream);

    }
    
    private void addFileToJar(File file, JarOutputStream jarOut) throws Exception {
        
        ZipEntry ze = new ZipEntry(file.getName());

        try {
            jarOut.putNextEntry(ze);
            FileInputStream inStream = new FileInputStream(file);
            byte[] fileContents = new byte[inStream.available()];
            inStream.read(fileContents);
            jarOut.write(fileContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void copyJar(File file, JarOutputStream jarOut) throws Exception {
        
        try {
            JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
            ZipEntry ze;
            byte[] readBuf = new byte[1000];
            int bytesRead;
            while ((ze = jarIn.getNextEntry()) != null) {
                if (ze.getName().equals("META-INF/MANIFEST.MF"))
                    continue;
                jarOut.putNextEntry(ze);
                while ((bytesRead = jarIn.read(readBuf)) > 0) {
                    jarOut.write(readBuf, 0, bytesRead);
                }
            }
            jarIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private InputStream createBundleManifest(File tuscanyDir, String bundleName, String bundleSymbolicName) throws Exception {
        
        File jarFile = new File(tuscanyDir.getPath() + File.separator + bundleName);
        if (!jarFile.exists())
            return null;
        JarInputStream jar = new JarInputStream(new FileInputStream(jarFile));
        Manifest manifest = jar.getManifest();
        if (manifest == null)
            manifest = new Manifest();
        
        boolean isImmutableJar = false;
        for (String immutableJar : immutableJars) {
            if (bundleName.startsWith(immutableJar)) {
                isImmutableJar = true;
                break;
            }
        }
        Attributes attributes = manifest.getMainAttributes();
        if (attributes.getValue("Manifest-Version") == null) {
            attributes.putValue("Manifest-Version", "1.0");
        }
        if (isImmutableJar)
            attributes.putValue("Bundle-ClassPath", bundleName);
        
        String packages = getPackagesInJar(bundleName, jar);
        String version = getJarVersion(bundleName);

        attributes.remove(new Attributes.Name("Require-Bundle"));
        attributes.remove(new Attributes.Name("Import-Package"));
        
        if (attributes.getValue("Bundle-SymbolicName") == null)
            attributes.putValue("Bundle-SymbolicName", bundleSymbolicName);
        if (attributes.getValue("Bundle-Version") == null)
            attributes.putValue("Bundle-Version", version);
        // Existing export statements in bundles may contain versions, so they should be used as is
        // SDO exports are not sufficient, and should be changed
        if (attributes.getValue("Export-Package") == null || bundleName.startsWith("tuscany-sdo-impl")) {
            attributes.putValue("Export-Package", packages);
            attributes.putValue("Import-Package", packages);
        }
        
        attributes.putValue("DynamicImport-Package", "*");       
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.write(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        out.close();
               
        return in;
        
    }
    
    private String getPackagesInJar(String bundleName, JarInputStream jar) throws Exception {
        HashSet<String> packages = new HashSet<String>();
        ZipEntry entry;
        while ((entry = jar.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (!entry.isDirectory() && entryName != null && entryName.length() > 0 && 
                    !entryName.startsWith(".") && !entryName.startsWith("META-INF") &&
                    entryName.lastIndexOf("/") > 0) {
                String pkg = entryName.substring(0, entryName.lastIndexOf("/")).replace('/', '.');
                packages.add(pkg);
                
            }
        }
        // FIXME: Split package
        if (bundleName.startsWith("axis2-adb"))
            packages.remove("org.apache.axis2.util");
        if (bundleName.startsWith("axis2-codegen")) {
            packages.remove("org.apache.axis2.wsdl");
            packages.remove("org.apache.axis2.wsdl.util");
        }
        
        StringBuilder pkgBuf = new StringBuilder();
        for (String pkg : packages) {
            if (pkgBuf.length() >0) pkgBuf.append(',');
            pkgBuf.append(pkg);
        }
        return pkgBuf.toString();
    }
    
    private String getJarVersion(String bundleName) {
        Pattern pattern = Pattern.compile("-([0-9.]+)");
        Matcher matcher = pattern.matcher(bundleName);
        String version = "1.0.0";
        if (matcher.find()) {
            version = matcher.group();
            if (version.endsWith("."))
                version = version.substring(1, version.length()-1);
            else
                version = version.substring(1);
        }
        return version;
    }
	
}