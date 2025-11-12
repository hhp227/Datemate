//
//  RegisterView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/28.
//

import SwiftUI

struct SignUpView: View {
    @ObservedObject var viewModel = SignUpViewModel(.init())
    
    @State var email = ""
    
    @State var password = ""
    
    var body: some View {
        VStack {
            VStack {
                TextField("Email", text: $email).padding().disableAutocorrection(true).autocapitalization(.none).background(Color(.secondarySystemBackground))
                SecureField("Password", text: $password).padding().disableAutocorrection(true).autocapitalization(.none).background(Color(.secondarySystemBackground))
                Button(action: {
                    viewModel.signUp(email: email, password: password)
                }, label: {
                    Text("Register").foregroundColor(Color.white).frame(width: 200, height: 50).cornerRadius(8).background(Color.blue)
                })
            }.padding()
        }
    }
}

struct SignUpView_Previews: PreviewProvider {
    static var previews: some View {
        SignUpView()
    }
}
