/* This file is part of the YAZ toolkit.
 * Copyright (C) Index Data
 * See the file LICENSE for details.
 */

#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <yaz/xmalloc.h>
#include <yaz/zoom.h>


void *thread_routine(void *arg)
{
    ZOOM_connection con = ZOOM_connection_create(0);

    ZOOM_connection_option_set(con, "user", "EXL");
    ZOOM_connection_option_set(con, "password", "4444");
    ZOOM_connection_option_set(con, "group", "SNUL");
    ZOOM_connection_option_set(con, "apdulog", "1");
    ZOOM_connection_option_set(con, "timeout", "30");
    ZOOM_connection_option_set(con, "preferredRecordSyntax", "XML");

    ZOOM_connection_connect(con, "localhost:9999", 0);

    void *retval = 0;
    const char *errorMessage = 0;
    const char *additionalInfo = 0;
    if (ZOOM_connection_error(con, &errorMessage, &additionalInfo))
    {
	retval = (void *) errorMessage;
    }
    if (retval == 0)
    {
	ZOOM_package pkg = ZOOM_connection_package(con, 0);
	ZOOM_package_option_set(pkg, "action", "recordInsert");
	ZOOM_package_option_set(pkg, "elementSetName", "F");
	ZOOM_package_option_set(pkg, "updateVersion", "2");
	const char *xml = "<?xml version=\"1.0\" ?><db_name>UBIB</db_name><job_type>INS</job_type><marc_data>02247nam#au200553zcb4500001001400000005001700014007000300031008004100034009001300075015002100088020001500109035001500124035002300139035002600162035002600188040002500214041000800239044001000247084005800257084005800315084005800373084005800431084006100489084006100550084006000611084006000671100005200731245016400783264004700947300001100994490007301005500003201078650006601110650006401176650006101240650007901301689003601380689004201416689003801458689004301496689001601539830003301555970000701588974002301595974002401618974001701642974002601659983000801685994300950012120180226085717.0tu920824|1992####gw############|||#|#ger#u  AC00489596  a92,N30,04772dnb  a3631447809  aAC00489596  a(AT-OBV)AC00489596  a(Aleph)000001931ONB01  a(DE-599)OBVAC00489596  aUBSbgerdUBSerakwb  ager  cXA-DE  a89.112bkl9O: Automatisch aus GBV_2011-10 2012-05-16  a89.212bkl9O: Automatisch aus GBV_2011-10 2012-05-16  a89.102bkl9O: Automatisch aus GBV_2011-10 2012-05-16  a15.432bkl9O: Automatisch aus GBV_2011-10 2012-05-16  aMG 150302rvk9O: Automatisch aus BVB_2013-06 2013-03-27  aMG 153802rvk9O: Automatisch aus BVB_2013-06 2013-03-27  aMC 72002rvk9O: Automatisch aus BVB_2013-06 2013-03-27  aMC 62002rvk9O: Automatisch aus BVB_2013-06 2013-03-271 aMantino, Susanned1963-0(DE-588)1209571834aut00a<<Die>> \"Neue Rechte\" in der \"Grauzone\" zwischen Rechtsextremismus und Konservatismusbeine systematische Analyse des Phänomens \"Neue Rechte\"cSusanne Mantino 1aFrankfurt am MainaWien [u.a.]bLangc1992  a200 S.1 aEuropäische Hochschulschriften : Reihe 31, Politikwissenschaftv199  aLiteraturverz. S. 189 - 200 0aConservatismaGermanyzAutomatisch aus GBV_2011-10 2012-05-16 0aRadicalismaGermanyzAutomatisch aus GBV_2011-10 2012-05-16 0aFascismaGermanyzAutomatisch aus GBV_2011-10 2012-05-16 0aRight and left (Political science)zAutomatisch aus GBV_2011-10 2012-05-1600aDeutschland.0(DE-588)4011882-401aRechtsradikalismus0(DE-588)4048829-902aNeue RechteDs0(DE-588)4120795-603aKonservativismusDs0(DE-588)4032187-30 5AT-OBV5UBS 0w(AT-OBV)AC00042235v1999O:11 c090sF030Az|1dcr|||||370sF050Aa|a|||||||||||0sF051As||||||0uV903a2134a3214a42310 a016</marc_data></xml>";
	ZOOM_package_option_set(pkg, "record", xml);
	int i;
	for (i = 0; i < 3 && retval == 0; i++)
	{
	    ZOOM_package_send(pkg, "update");
	    if (ZOOM_connection_error(con, &errorMessage, &additionalInfo))
	    {
		retval = (void *) errorMessage;
	    }
	}
	ZOOM_package_destroy(pkg);
    }
    ZOOM_connection_destroy(con);
    return retval;
}

int main(int argc, char **argv)
{
    int no = 10;
    pthread_t *pids = malloc(no * sizeof(*pids));
    int i;
    for (i = 0; i < no; i++)
    {
	if (pthread_create(pids + i, 0 /*attr*/, thread_routine, 0) != 0)
	{
	    fprintf(stderr, "couln't create thread no %d\n", i);
	    exit(1);
	}
    }
    for (i = 0; i < no; i++)
    {
	void *retval = 0;
	pthread_join(pids[i], &retval);
	if (retval != 0)
	{
	    const char *msg = retval;
	    fprintf(stderr, "Thread %d returned error %s\n", i, msg);
	}
    }
    free(pids);
    return 0;
}
