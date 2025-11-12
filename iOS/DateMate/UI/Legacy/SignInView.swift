//
//  LoginView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/27.
//

import SwiftUI

struct SignInView: View {
    @EnvironmentObject var viewModel: SignInViewModel
    
    @State var email = ""
    
    @State var password = ""
    
    var body: some View {
        VStack {
            VStack {
                Image("logo_light_eng").resizable().aspectRatio(contentMode: .fit).padding()
                Text("Find your datemate")
            }
            VStack {
                TextField("Email", text: $email).disableAutocorrection(true).autocapitalization(.none).padding().background(Color(.secondarySystemBackground))
                SecureField("Password", text: $password).disableAutocorrection(true).autocapitalization(.none).padding().background(Color(.secondarySystemBackground))
                Button(action: {
                    viewModel.signIn(email: email, password: password)
                }, label: {
                    Text("Sign In").foregroundColor(Color.white).padding().frame(maxWidth:.infinity).cornerRadius(8).background(Color.blue)
                })
                NavigationLink("Sign Up", destination: SignUpView())
            }.padding()
            Spacer()
        }.frame(minWidth:0, maxWidth:.infinity, alignment: .top)
    }
}

struct SignInView_Previews: PreviewProvider {
    static var previews: some View {
        SignInView().preferredColorScheme(.dark)
    }
}
