Name:       tinyweb
Summary:    test
Version:    0.22
Release:    1
Group:      Development/Debug
License:    GPL v2 only
URL:        http://www.tizen.org/
Source0:    %{name}-%{version}.tar.gz
BuildRoot:  %{_tmppath}/%{name}-%{version}-build

%description
A lightweight web server. It support http directory, websocket, cgi etc.


%prep
%setup -q -n %{name}-%{version}
# >> setup
# << setup

%build
# >> build pre
# << build pre


# Call make instruction with smp support
make %{?jobs:-j%jobs}

# >> build post
# << build post
%install
rm -rf %{buildroot}
# >> install pre
# << install pre
%make_install

# >> install post
# << install post

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)
# >> files
%{_bindir}/tinyweb
%{_libdir}/echo.so
%{_libdir}/libmongoose.so
%{_datadir}/%{name}/server.pem

%{_libdir}/
# << files

%changelog
* Tue Mar  21 2013 jing wang <jing.j.wang@intel.com> 0.22
- create for tct2.1 build
