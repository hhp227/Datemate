//
//  ContentView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/24.
//

import SwiftUI
import Firebase

struct ContentView: View {
    @State var signInState: SignInState = .loading

    var body: some View {
        Group {
            switch signInState {
            case .signIn:
                AppNavigation()
            case .signOut:
                SignInNavigation()
            case .loading:
                EmptyView()
            }
        }
        .onReceive(DependencyContainer.instance.userRepository.signInStatePublisher) {
            signInState = $0
        }
    }
}

struct AppNavigation: View {
    @State private var path: [String] = []
    
    var body: some View {
        NavigationStack(path: $path) {
            MainView(
                onNavigateToSubFirst: { data in
                    path.append("sub_first:\(data)")
                },
                onNavigateToSubSecond: {
                    path.append("sub_second")
                }
            )
            .navigationDestination(for: String.self) { route in
                if route.starts(with: "sub_first:") {
                    let data = route.replacingOccurrences(of: "sub_first:", with: "")
                    SubFirstView(viewModel: DependencyContainer.instance.provideDetailViewModel(data: data))
                } else if route == "sub_second" {
                    PostDetailView(
                        //onNavigateUp = { path.removeLast() }
                    )
                }
            }
        }
    }
}

struct SignInNavigation: View {
    @State private var path: [String] = []
    
    var body: some View {
        NavigationStack(path: $path) {
            SignInView(
                onSignUp: { path.append("sign_up") },
                onForgotPassword: { path.append("forgot_password") }
            )
            .navigationDestination(for: String.self) { route in
                if route == "sign_up" {
                    SignUpView {
                        path.append("gender_setup")
                    } onBackToSignIn: {
                        path = []
                    }
                } else if route == "gender_setup" {
                    GenderSetupView(onNext: { path.append("photo_setup") })
                } else if route == "photo_setup" {
                    PhotoSetupView(onNext: { path.append("info_setup") })
                } else if route == "info_setup" {
                    InfoSetupView(onSetupComplete: {
                        path.removeAll()
                    })
                } else if route == "forgot_password" {
                    ForgotPasswordView(onBackToSignIn: { path.removeLast() })
                }
            }
        }
    }
}

/*struct RootNavigation: View {
    @State private var path: [String] = []

    var body: some View {
        NavigationStack(path: $path) {
            MainView2(onNavigate: { route in
                path.append(route)
            })
            .navigationDestination(for: String.self) { route in
                switch route {
                case let str where str.starts(with: "sub_first/"):
                    let data = str.replacingOccurrences(of: "sub_first/", with: "")
                    SubFirstView(viewModel: SubFirstViewModel(data: data))
                case "sub_second":
                    SubSecondView()
                default:
                    EmptyView()
                }
            }
        }
    }
}*/

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

/*struct ContentView: View {
    var body: some View {
        Home()
    }
}

struct Home: View {
    @State var show = false
    
    @State var status = UserDefaults.standard.value(forKey: "status") as? Bool ?? false
    
    var body: some View {
        NavigationView {
            VStack {
                if self.status {
                    HomeScreen()
                } else {
                    ZStack {
                        NavigationLink(destination: SignUp(show: self.$show), isActive: self.$show) {
                            Text("")
                        }.hidden()
                        Login(show: self.$show)
                    }
                }
            }.navigationTitle("")
                .navigationBarHidden(true)
                .navigationBarBackButtonHidden(true)
                .onAppear {
                    NotificationCenter.default.addObserver(forName: NSNotification.Name("status"), object: nil, queue: .main) { _ in
                        self.status = UserDefaults.standard.value(forKey: "status") as? Bool ?? false
                    }
                }
        }
    }
}

struct HomeScreen: View {
    var body: some View {
        VStack {
            Text("Logged Successfully").font(.title)
                .fontWeight(.bold)
                .foregroundColor(.black.opacity(0.7))
            Button(action: {
                try! Auth.auth().signOut()
                UserDefaults.standard.set(false, forKey: "status")
                NotificationCenter.default.post(name: NSNotification.Name("status"), object: nil)
            }, label: {
                Text("Log out").foregroundColor(.white)
                    .padding(.vertical)
                    .frame(width: UIScreen.main.bounds.width - 50)
            }).background(Color.pink)
                .cornerRadius(10)
                .padding(.top, 25)
        }
    }
}

struct Login: View {
    @State var color = Color.black.opacity(0.7)
    
    @State var email = ""
    
    @State var pass = ""
    
    @State var visible = false
    
    @Binding var show: Bool
    
    @State var alert = false
    
    @State var error = ""
    
    var body: some View {
        ZStack {
            ZStack(alignment: .topTrailing) {
                GeometryReader { _ in
                    VStack {
                        Image("icon").scaledToFit()
                        Text("Log in to your account").font(.title)
                            .fontWeight(.bold)
                            .foregroundColor(self.color)
                            .padding(.top, 35)
                        TextField("Email", text: self.$email).autocapitalization(.none)
                            .padding()
                            .background(RoundedRectangle(cornerRadius: 4).stroke(self.email != "" ? Color.pink : self.color, lineWidth: 2))
                            .padding(.top, 25)
                        HStack(spacing: 15) {
                            VStack {
                                if self.visible {
                                    TextField("Password", text: self.$pass).autocapitalization(.none)
                                } else {
                                    SecureField("Password", text: self.$pass).autocapitalization(.none)
                                }
                            }
                            Button(action: {
                                self.visible.toggle()
                            }, label: {
                                Image(systemName: self.visible ? "eye.slash.fill" : "eye.fill").foregroundColor(self.color)
                            })
                        }.padding()
                            .background(RoundedRectangle(cornerRadius: 4).stroke(self.pass != "" ? Color.pink : self.color, lineWidth: 2))
                            .padding(.top, 25)
                        HStack {
                            Spacer()
                            Button(action: {
                                self.reset()
                            }, label: {
                                Text("Forget password").fontWeight(.bold)
                                    .foregroundColor(Color.pink)
                            })
                        }.padding(.top, 20)
                        Button(action: {
                            self.verify()
                        }, label: {
                            Text("Log in").foregroundColor(.white)
                                .padding(.vertical)
                                .frame(width: UIScreen.main.bounds.width - 50)
                        }).background(Color.pink)
                            .cornerRadius(10)
                            .padding(.top, 25)
                    }.padding(.horizontal, 25)
                }
                Button(action: {
                    self.show.toggle()
                }, label: {
                    Text("Register").fontWeight(.bold)
                        .foregroundColor(Color.pink)
                }).padding()
            }
            if self.alert {
                ErrorView(alert: self.$alert, error: self.$error)
            }
        }
    }
    
    func verify() {
        if self.email != "" && self.pass != "" {
            Auth.auth().signIn(withEmail: self.email, password: self.pass) { (res, err) in
                if err != nil {
                    self.error = err!.localizedDescription
                    self.alert.toggle()
                    return
                }
                print("success")
                UserDefaults.standard.set(true, forKey: "status")
                NotificationCenter.default.post(name: NSNotification.Name("status"), object: nil)
            }
        } else {
            self.error = "Please fill all the contents properly"
            self.alert.toggle()
        }
    }
    
    func reset() {
        if self.email != "" {
            Auth.auth().sendPasswordReset(withEmail: self.email) { err in
                if err != nil {
                    self.error = err!.localizedDescription
                    self.alert.toggle()
                    return
                }
                self.error = "RESET"
                self.alert.toggle()
            }
        } else {
            self.error = "Email Id is empty"
            self.alert.toggle()
        }
    }
}

struct SignUp: View {
    @State var color = Color.black.opacity(0.7)
    
    @State var email = ""
    
    @State var pass = ""
    
    @State var repass = ""
    
    @State var visible = false
    
    @State var revisible = false
    
    @Binding var show: Bool
    
    @State var alert = false
    
    @State var error = ""
    
    var body: some View {
        ZStack {
            ZStack(alignment: .topLeading) {
                GeometryReader { _ in
                    VStack {
                        Image("icon").scaledToFit()
                        Text("Log in to your account").font(.title)
                            .fontWeight(.bold)
                            .foregroundColor(self.color)
                            .padding(.top, 35)
                        TextField("Email", text: self.$email).autocapitalization(.none)
                            .padding()
                            .background(RoundedRectangle(cornerRadius: 4).stroke(self.email != "" ? Color.pink : self.color, lineWidth: 2))
                            .padding(.top, 25)
                        HStack(spacing: 15) {
                            VStack {
                                if self.visible {
                                    TextField("Password", text: self.$pass).autocapitalization(.none)
                                } else {
                                    SecureField("Password", text: self.$pass).autocapitalization(.none)
                                }
                            }
                            Button(action: {
                                self.visible.toggle()
                            }, label: {
                                Image(systemName: self.visible ? "eye.slash.fill" : "eye.fill").foregroundColor(self.color)
                            })
                        }.padding()
                            .background(RoundedRectangle(cornerRadius: 4).stroke(self.pass != "" ? Color.pink : self.color, lineWidth: 2))
                            .padding(.top, 25)
                        HStack(spacing: 15) {
                            VStack {
                                if self.revisible {
                                    TextField("Re-enter", text: self.$repass).autocapitalization(.none)
                                } else {
                                    SecureField("Re-enter", text: self.$repass).autocapitalization(.none)
                                }
                            }
                            Button(action: {
                                self.revisible.toggle()
                            }, label: {
                                Image(systemName: self.revisible ? "eye.slash.fill" : "eye.fill").foregroundColor(self.color)
                            })
                        }.padding()
                            .background(RoundedRectangle(cornerRadius: 4).stroke(self.repass != "" ? Color.pink : self.color, lineWidth: 2))
                            .padding(.top, 25)
                        Button(action: {
                            self.register()
                        }, label: {
                            Text("Register").foregroundColor(.white)
                                .padding(.vertical)
                                .frame(width: UIScreen.main.bounds.width - 50)
                        }).background(Color.pink)
                            .cornerRadius(10)
                            .padding(.top, 25)
                    }.padding(.horizontal, 25)
                }
                Button(action: {
                    self.show.toggle()
                }, label: {
                    Image(systemName: "chevron.left").font(.title)
                        .foregroundColor(Color.pink)
                }).padding()
            }
            if self.alert {
                ErrorView(alert: self.$alert, error: self.$error)
            }
        }.navigationBarBackButtonHidden(true)
    }
    
    func register() {
        if self.email != "" {
            if self.pass == self.repass {
                Auth.auth().createUser(withEmail: self.email, password: self.pass) { res, err in
                    if err != nil {
                        self.error = err!.localizedDescription
                        self.alert.toggle()
                        return
                    }
                    print("success")
                    UserDefaults.standard.set(true, forKey: "status")
                    NotificationCenter.default.post(name: NSNotification.Name("status"), object: nil)
                }
            } else {
                self.error = "Password Mismatch"
                self.alert.toggle()
            }
        } else {
            self.error = "Please fill all the contents properly"
            self.alert.toggle()
        }
    }
}

struct ErrorView: View {
    @State var color = Color.black.opacity(0.7)
    
    @Binding var alert: Bool
    
    @Binding var error: String
    
    var body: some View {
        GeometryReader { _ in
            VStack {
                HStack {
                    Text(self.error == "RESET" ? "Message" : "Error").font(.title)
                        .fontWeight(.bold)
                        .foregroundColor(self.color)
                    Spacer()
                }.padding(.horizontal, 25)
                Text(self.error == "RESET" ? "Password reset link has been sent successfully" : self.error)
                    .foregroundColor(self.color)
                    .padding(.top)
                    .padding(.horizontal, 25)
                Button(action: {
                    self.alert.toggle()
                }, label: {
                    Text(self.error == "RESET" ? "Ok" : "Cancel").foregroundColor(.white)
                        .padding(.vertical)
                        .frame(width: UIScreen.main.bounds.width - 120)
                }).background(Color.pink)
                    .cornerRadius(10)
                    .padding(.top, 25)
            }.padding(.vertical, 25)
                .frame(width: UIScreen.main.bounds.width - 70)
                .background(Color.white)
                .cornerRadius(15)
        }.background(Color.black.opacity(0.35).edgesIgnoringSafeArea(.all))
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}*/
