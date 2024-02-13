To run the multiple values param in jasper report:
1) Create a report in jasper studio.
2) Edit the source of the report like below. Add these line below style definition and above field definition.
.....
<parameter name="module" class="java.util.List">
      <defaultValueExpression><![CDATA[new ArrayList(Arrays.asList(new String[]{"Retail", "Payment"}))]]></defaultValueExpression>
	</parameter>
	<queryString language="SQL">
		<![CDATA[SELECT *
FROM idgc_alerts_seed_attribute WHERE $X{IN, module, module}]]>
</queryString>
.......
{In this script I want to take module as a list in input from idgc_alerts_seed_attribute table. So I configured accordingly}
3) Preview the report and see if its working properly.
4) publish the report on jasper server with datasource defined on server.
5) Check the report working on server.

Do below things to fetch values from table itself:
1) Create a new folder Input Control.
2) Under that Add Resource->Input Control.
3) Select type as Multi-Select Query. Prompt text and values according to your need.
4) Define Query in next step-> Name and resource id accordingly.
5) Select data source.
6) Write a query with DISTICT clause. 
7) My Query:"select distinct module from idgc_alerts_seed_attribute"
8) Set value column and visible column accordingly.
9) And save it.
10) Now edit your report-> Control & Resources-> 
11) Select created input control in Input Controls and submit

--------------------------For pagination-----------------------------
To do pagination and to add date at footer add the following script at end or before summary in jrxml file of your report
........
<pageFooter>
    <band height="25" splitType="Stretch">
        <frame>
            <reportElement mode="Opaque" x="0" y="1" width="555" height="24" forecolor="#D0B48E" backcolor="#000000" uuid="729c3539-f946-4d0e-a0a7-bda2815ea1b0"/>
            <textField evaluationTime="Report">
            <reportElement style="Column header" x="513" y="0" width="40" height="20" forecolor="#FFFFFF" uuid="4834e99c-7f6c-485a-b098-50e5187f2ab4"/>
                <textElement verticalAlignment="Middle">
                    <font size="10" isBold="false"/>
                </textElement>
            <textFieldExpression><![CDATA[" " + $V{PAGE_NUMBER}]]></textFieldExpression>
            </textField>
            <textField>
            <reportElement style="Column header" x="433" y="0" width="80" height="20" forecolor="#FFFFFF" uuid="9825b46e-76f1-469a-abb8-a1cc27aad685"/>
                <textElement textAlignment="Right" verticalAlignment="Middle">
                    <font size="10" isBold="false"/>
                </textElement>
                <textFieldExpression><![CDATA["Page "+$V{PAGE_NUMBER}+" of"]]></textFieldExpression>
            </textField>
            <textField pattern="EEEEE dd MMMMM yyyy">
            <reportElement style="Column header" x="2" y="1" width="197" height="20" forecolor="#FFFFFF" uuid="137d38c7-1a83-4fc5-b156-9d5f1b1a0ddc"/>
                <textElement verticalAlignment="Middle">
                    <font size="10" isBold="false"/>
                </textElement>
                <textFieldExpression><![CDATA[new java.util.Date()]]></textFieldExpression>
            </textField>
        </frame>
<   /band>
</pageFooter>