��������:
	- ��� ������� ������ ������������� Spring framework
	- ��� �������� ������ ������������� ���� ������ MySql � ���������� hybernate � Spring repositories
	- ����� 'Client' ��������� ������� � ��� ��������� � ��������� ������
	- ����� 'CaptchaTask' ��������� CAPTCHA ������� � ��� ���������������, ������� � ����������� ����������� �������. ����� 'getImage()' �� ������� ���������� ����������� �� ������ �� �������
	- ����� 'Token' ��������� ����� ������� �������� ��� ����������� ��� ������ ������� CAPTCHA ������� � ���������� ��������� ������ � ������������ �������
	- ���������� ���������� ��� 'Repository' ��������� Spring CrudRepository ��� ������� ��� ������� ����� ��������� � ���� ������ � �������������� ��� ��������� ��������
	- ����� ClientController �������� ������������, ������� ����� ������������ ������ �� ����������� ������
	- ����� CaptchaController �������� ������������, ������� ����� ������������ ��� ������� ��������� � ���������� �������������� CAPTCHA �������, ���������� ����������� �������, ��������� ������ �� ������� � ������ ������, � ������ ���������� ���� ����������� �������, � �������� �� ����������� ������
	- � ������ ��������� ����� ���� ������� �������, ����� ������������ ������������� ����������� �� ������ 
	- ��� �������� ���������� ���� 'InconsistencyException' ����� ������������ ����� �� �������� ������ 409 'CONFLICT'. � ������� ������������� CAPTCHA ������� ��� ������ ����������� �������������� �������
	- ��� �������� ���������� ���� 'NotFoundException' ����� ������������ ����� �� �������� ������ 404 'Not found'. � ������� ������� �������������� ������� ��� CAPTCHA ������� ��� ������ �����������
	- ��� �������� ���������� ���� 'TimeOutException' ����� ������������ ����� �� �������� ������ 408 'Request time out'. � �������, ����� ����� ����� CAPTCHA ������� �������
	- ��������� �������� 'ttl' ����� ����� CAPTCHA ����� � ��������
	- ����� 'CaptchaControllerTest' ��������� ��������� ����� ����������� ��� ��������� ������ ��� �������� �������������� ������� 'CaptchaController', ����� ������ � ��������� ���������� �������
	- ����� 'ClientControllerTest' ��������� ��������� ����� ����������� ��� ��������� ������ ��� �������� �������������� ������� 'ClientController', ����� ������ � ��������� ���������� �������

�������������:
	- ��� ��� �������� ����������� ��������� ���������� 'production' �� ������ � �������, � �������, ��� �������� '0' ����� ������������� ������ ������������, '1' ����� ������������ ������� ������

����������:
	- ����� �������� ��������� ��� ������ IntelliJ IDEA
	- G��������� ���������� c �������������� Apache Maven
	- ���������� ����������� � �������������� Spring Boot
	- ���������� ���� ������ MySql
	- � ����� �������� 'application.properties' ����� ������� ������ �� ���� � ��������� �����������, �������� 'spring.jpa.hibernate.ddl-auto' ������������� ��� 'create' - ����� ������������� ���� � ��������� � ������ ������������ ����������, 'none' - �� ����� ����������� ����������� ��������� � ������������������� ������