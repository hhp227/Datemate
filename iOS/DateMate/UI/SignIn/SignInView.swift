//
//  LoginView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/27.
//

import SwiftUI

struct SignInView: View {
    @StateObject private var viewModel = DependencyContainer.instance.provideSignInViewModel()
    
    var onSignUp: () -> Void
    
    @FocusState private var focusedField: Field?

        enum Field {
            case email
            case password
        }
    
    /*var body: some View {
        VStack {
            VStack {
                Image("logo_light_eng").resizable().aspectRatio(contentMode: .fit).padding()
                Text("Find your datemate")
            }
            VStack {
                TextField("Email", text: Binding(get: { viewModel.uiState.email }, set: viewModel.onEmailChanged)).disableAutocorrection(true).autocapitalization(.none).padding().background(Color(.secondarySystemBackground))
                SecureField("Password", text: Binding(get: { viewModel.uiState.password }, set: viewModel.onPasswordChanged)).disableAutocorrection(true).autocapitalization(.none).padding().background(Color(.secondarySystemBackground))
                Button(action: {
                    viewModel.signIn(email: viewModel.uiState.email, password: viewModel.uiState.password)
                }, label: {
                    Text("Sign In").foregroundColor(Color.white).padding().frame(maxWidth:.infinity).cornerRadius(8).background(Color.blue)
                })
                NavigationLink("Sign Up", destination: SignUpView())
            }.padding()
            Spacer()
        }
        .frame(minWidth:0, maxWidth:.infinity, alignment: .top)
    }*/
    var body: some View {
            VStack {
                VStack {
                    Logo().padding(.horizontal, 76)
                    Text("Find your datemate")
                        .font(.subheadline)
                        .multilineTextAlignment(.center)
                        .padding(.top, 24)
                        .frame(maxWidth: .infinity)
                }
                .padding(.top, 20)

                // Email, Password, Buttons
                VStack(alignment: .center) {
                    EmailField(
                        value: viewModel.uiState.email,
                        error: viewModel.uiState.emailError,
                        onValueChange: viewModel.onEmailChanged,
                        onSubmit: { focusedField = .password }
                    )
                    .focused($focusedField, equals: .email)

                    Spacer().frame(height: 16)

                    PasswordField(
                        value: viewModel.uiState.password,
                        error: viewModel.uiState.passwordError,
                        onValueChange: viewModel.onPasswordChanged,
                        onSubmit: {
                            viewModel.signIn(
                                email: viewModel.uiState.email,
                                password: viewModel.uiState.password
                            )
                        }
                    )
                    .focused($focusedField, equals: .password)

                    Spacer().frame(height: 16)

                    Button(action: {
                        viewModel.signIn(
                            email: viewModel.uiState.email,
                            password: viewModel.uiState.password
                        )
                    }) {
                        Text("Sign In")
                            .frame(maxWidth: .infinity)
                    }
                    .padding(.vertical, 16)
                    .buttonStyle(.borderedProminent)
                    .disabled(!viewModel.uiState.isSignInEnabled)

                    Spacer().frame(height: 12)

                    Button(action: onSignUp) {
                        Text("Sign Up")
                    }
                    .padding(.top, 8)

                }
                .padding(.horizontal, 16)

                Spacer()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
}

struct Logo: View {
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        Image(colorScheme == .light ? "logo_light_eng" : "logo_dark_eng")
            .resizable()
            .aspectRatio(contentMode: .fit)
            .padding()
    }
}

struct EmailField: View {
    let value: String
    
    let error: String?
    
    let onValueChange: (String) -> Void
    
    var onSubmit: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            TextField("Email", text: Binding(
                get: { value },
                set: { onValueChange($0) }
            ))
            .textContentType(.emailAddress)
            .keyboardType(.emailAddress)
            .submitLabel(.next)
            .onSubmit { onSubmit() }
            .padding()
            .background(RoundedRectangle(cornerRadius: 8).stroke(error == nil ? Color.gray : Color.red))
            if let err = error {
                TextFieldError(textError: err)
            }
        }
    }
}

struct PasswordField: View {
    @State private var showPassword = false
    
    let value: String
    
    let error: String?
    
    let onValueChange: (String) -> Void
    
    var onSubmit: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                if showPassword {
                    TextField("Password", text: Binding(
                        get: { value },
                        set: { onValueChange($0) }
                    ))
                    .submitLabel(.done)
                    .onSubmit { onSubmit() }
                } else {
                    SecureField("Password", text: Binding(
                        get: { value },
                        set: { onValueChange($0) }
                    ))
                    .submitLabel(.done)
                    .onSubmit { onSubmit() }
                }
                Button(action: { showPassword.toggle() }) {
                    Image(systemName: showPassword ? "eye.slash" : "eye")
                }
            }
            .padding()
            .background(RoundedRectangle(cornerRadius: 8).stroke(error == nil ? Color.gray : Color.red))
            if let err = error {
                TextFieldError(textError: err)
            }
        }
    }
}

struct TextFieldError: View {
    let textError: String

    var body: some View {
        Text(textError)
            .foregroundColor(.red)
            .font(.caption)
            .padding(.leading, 4)
    }
}

struct SignInView_Previews: PreviewProvider {
    static var previews: some View {
        SignInView(onSignUp: {}).preferredColorScheme(.dark)
    }
}
