package org.yaz4j;

import org.yaz4j.ConnectionExtended;
import org.yaz4j.exception.ZoomException;

class Z39Publisher implements Runnable {

    private Thread t;
    private String threadName;
    private static final String UPDATE_VERSION = "2";
    private static final String UPDATE_VERSION_PARAM = "updateVersion";
    private static final String ELEMENT_SET_PARAM = "elementSetName";
    private static final String ACTION_PARAM = "action";
    private static final String ELEMENT_SET_NAME = "F";
    private static final String ACTION = "recordInsert";
    private static final String PACKAGE_TYPE = "update";
    private boolean completed = false;

    Z39Publisher(String name) {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    @Override
    public void run() {
        System.out.println("Running " + threadName);
        try {
            publish();
        } catch (ZoomException e) {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    private void publish() throws ZoomException {

        int port = 0;

        port = 9008;

        //ConnectionExtended con = new ConnectionExtended("zserver.riss4u.net", port);
        
        ConnectionExtended con = new ConnectionExtended("localhost:9999", 0);

        try {
            con.setUsername("EXL");
            con.setPassword("4444");
            con.option("group", "SNUL");
            //con.setDatabaseName("UBIB");
            con.option("apdufile", "apdu.log");
            con.option("timeout", "30");
            con.setSyntax("XML");
            System.out.println("connecting with " + threadName);
            con.connect();
            System.out.println("connected with " + threadName);
            org.yaz4j.Package p = con.getPackage(PACKAGE_TYPE);
            p.option(ACTION_PARAM, ACTION);
            p.option(ELEMENT_SET_PARAM, ELEMENT_SET_NAME);
            p.option(UPDATE_VERSION_PARAM, UPDATE_VERSION);
            for (int i = 0; i < 10; i++) {

                String xml = "<?xml version=\"1.0\" ?><db_name>UBIB</db_name><job_type>INS</job_type><marc_data>02247nam#au200553zcb4500001001400000005001700014007000300031008004100034009001300075015002100088020001500109035001500124035002300139035002600162035002600188040002500214041000800239044001000247084005800257084005800315084005800373084005800431084006100489084006100550084006000611084006000671100005200731245016400783264004700947300001100994490007301005500003201078650006601110650006401176650006101240650007901301689003601380689004201416689003801458689004301496689001601539830003301555970000701588974002301595974002401618974001701642974002601659983000801685994300950012120180226085717.0tu920824|1992####gw############|||#|#ger#u  AC00489596  a92,N30,04772dnb  a3631447809  aAC00489596  a(AT-OBV)AC00489596  a(Aleph)000001931ONB01  a(DE-599)OBVAC00489596  aUBSbgerdUBSerakwb  ager  cXA-DE  a89.112bkl9O: Automatisch aus GBV_2011-10 2012-05-16  a89.212bkl9O: Automatisch aus GBV_2011-10 2012-05-16  a89.102bkl9O: Automatisch aus GBV_2011-10 2012-05-16  a15.432bkl9O: Automatisch aus GBV_2011-10 2012-05-16  aMG 150302rvk9O: Automatisch aus BVB_2013-06 2013-03-27  aMG 153802rvk9O: Automatisch aus BVB_2013-06 2013-03-27  aMC 72002rvk9O: Automatisch aus BVB_2013-06 2013-03-27  aMC 62002rvk9O: Automatisch aus BVB_2013-06 2013-03-271 aMantino, Susanned1963-0(DE-588)1209571834aut00a<<Die>> \"Neue Rechte\" in der \"Grauzone\" zwischen Rechtsextremismus und Konservatismusbeine systematische Analyse des Phänomens \"Neue Rechte\"cSusanne Mantino 1aFrankfurt am MainaWien [u.a.]bLangc1992  a200 S.1 aEuropäische Hochschulschriften : Reihe 31, Politikwissenschaftv199  aLiteraturverz. S. 189 - 200 0aConservatismaGermanyzAutomatisch aus GBV_2011-10 2012-05-16 0aRadicalismaGermanyzAutomatisch aus GBV_2011-10 2012-05-16 0aFascismaGermanyzAutomatisch aus GBV_2011-10 2012-05-16 0aRight and left (Political science)zAutomatisch aus GBV_2011-10 2012-05-1600aDeutschland.0(DE-588)4011882-401aRechtsradikalismus0(DE-588)4048829-902aNeue RechteDs0(DE-588)4120795-603aKonservativismusDs0(DE-588)4032187-30 5AT-OBV5UBS 0w(AT-OBV)AC00042235v1999O:11 c090sF030Az|1dcr|||||370sF050Aa|a|||||||||||0sF051As||||||0uV903a2134a3214a42310 a016</marc_data></xml>";
                p.option("record", xml);
                System.out.println("sending package with " + threadName);
                p.send();
                System.out.println("sent package with " + threadName);
            }
            Query q = new PrefixQuery("@attr 1=4 water");
            con.search(q);
            completed = true;
        } finally {
            con.close();
        }
    }
    
    public boolean join() {
      try {
        t.join();
      } catch (InterruptedException ex) {
        return false;
      }
      return completed;
    }

}