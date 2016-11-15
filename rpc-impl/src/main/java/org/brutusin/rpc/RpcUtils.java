/*
 * Copyright 2015 brutusin.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brutusin.rpc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.brutusin.commons.utils.Miscellaneous;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class RpcUtils {

    private static final Logger LOGGER = Logger.getLogger(RpcWebInitializer.class.getName());
    private static final String VERSION = loadVersion();
    private static final String DESCRIPTION = loadDescription();

    private RpcUtils() {
    }

    private static String loadVersion() {
        try {
            String ret = Miscellaneous.toString(RpcUtils.class.getClassLoader().getResourceAsStream("brutusin-rpc.version"), "UTF-8");
            if (ret == null) {
                LOGGER.warning("Resource not found: 'brutusin-rpc.version'. Add this resource to improve API documentation");
            }
            return ret;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String loadDescription() {
        try {
            String ret = Miscellaneous.toString(RpcUtils.class.getClassLoader().getResourceAsStream("brutusin-rpc.md"), "UTF-8");
            if (ret == null) {
                LOGGER.warning("Resource not found: 'brutusin-rpc.md'. Add this resource to improve API documentation");
            }
            return ret;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    /**
     *
     * @param sc
     * @return
     */
    public static RpcSpringContext getSpringContext(ServletContext sc) {
        return (RpcSpringContext) sc.getAttribute(RpcSpringContext.class.getName());
    }

    public static Set<String> getUserRoles(Object securityContext) {
        Set<String> roleSet = new TreeSet<String>();
        if (securityContext != null) {
            SecurityContext sc = (SecurityContext) securityContext;
            if (sc.getAuthentication() != null) {
                Collection<? extends GrantedAuthority> authorities = sc.getAuthentication().getAuthorities();
                if (authorities != null) {
                    for (GrantedAuthority authority : authorities) {
                        String auth = authority.getAuthority();
                        if (auth.startsWith("ROLE_")) {
                            auth = auth.substring(5);
                        }
                        roleSet.add(auth);
                    }
                }
            }
        }
        return Collections.unmodifiableSet(roleSet);
    }

    public static boolean doOriginsMatch(String origin1, String origin2) {
        try {
            if (origin1 == null || origin2 == null) {
                return false;
            }
            if (origin1.equals("*") || origin2.equals("*")) {
                return true;
            }
            URI uri1 = getURI(origin1);
            URI uri2 = getURI(origin2);
            if (!uri1.getHost().equals(uri2.getHost())) {
                return false;
            }
            return getPort(uri1) == getPort(uri2);
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private static int getPort(URI uri) {
        if (uri.getPort() >= 0) {
            return uri.getPort();
        }
        if ("http".equals(uri.getScheme())) {
            return 80;
        }
        if ("https".equals(uri.getScheme())) {
            return 443;
        }
        return uri.getPort();
    }

    private static URI getURI(String origin) throws URISyntaxException {
        if (origin.startsWith("http://") || origin.startsWith("https://")) {
            return new URI(origin);
        }
        return new URI("http://" + origin);
    }
}
