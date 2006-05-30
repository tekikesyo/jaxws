#!/bin/sh

#
# $Id: wsgen.sh,v 1.2 2006-05-30 23:56:58 jitu Exp $
#

#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the "License").  You may not use this file except
# in compliance with the License.
# 
# You can obtain a copy of the license at
# https://jwsdp.dev.java.net/CDDLv1.0.html
# See the License for the specific language governing
# permissions and limitations under the License.
# 
# When distributing Covered Code, include this CDDL
# HEADER in each file and include the License file at
# https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
# add the following below this CDDL HEADER, with the
# fields enclosed by brackets "[]" replaced with your
# own identifying information: Portions Copyright [yyyy]
# [name of copyright owner]
#

if [ -z "$JAVA_HOME" ]; then
    echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.5)"
    exit 1
fi

bin_dir=`dirname $0`
WEBSERVICES_LIB=`cd $bin_dir/../share/lib; pwd`

CLASSPATH=$JAVA_HOME/lib/tools.jar:$WEBSERVICES_LIB/activation.jar:$WEBSERVICES_LIB/../jaxb/lib/jaxb-xjc.jar:$WEBSERVICES_LIB/jaxws-tools.jar

exec $JAVA_HOME/bin/java $WSGEN_OPTS -cp "$CLASSPATH" com.sun.tools.ws.WsGen "$@"
